package com.smokeythebandicoot.witcherycompanion.mixins.rite.effect;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.SpiritEffectRecipeProcessor;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritEffect;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import net.msrandom.witchery.resources.SpiritEffectManager;
import net.msrandom.witchery.rite.effect.RiteEffect;
import net.msrandom.witchery.rite.effect.RiteEffectBindSpiritsToFetish;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

/**
 * Mixins:
 * [Feature] unlock secret SpiritEffectRecipes
 */
@Mixin(RiteEffectBindSpiritsToFetish.class)
public abstract class RiteEffectBindSpiritsToFetishMixin extends RiteEffect {

    @Shadow(remap = false) @Final
    private int radius;

    @Shadow(remap = false) @Final
    private long fetish;

    /** This Mixin unlocks progress for a SpiritEffectRecipe if it is secret. The progress is awarded to
     * the player who activated the ritual */
    @Inject(method = "process", remap = false, cancellable = true, at = @At(value = "HEAD"))
    private void unlockSecret(World world, BlockPos pos, int ticks, AtomicInteger stage, TileEntityCircle.ActivatedRitual ritual, CallbackInfoReturnable<Result> cir) {

        ListMultimap<Class<? extends EntityCreature>, EntityCreature> ghosts = ArrayListMultimap.create();

        for (EntityCreature entity : world.getEntitiesWithinAABB(EntityCreature.class,
                new AxisAlignedBB(
                        pos.add(-this.radius, -this.radius, -this.radius),
                        pos.add(this.radius, this.radius, this.radius)))) {
            ghosts.put(entity.getClass(), entity);
        }

        SacrificedItem sacrificedItem = ritual.sacrificedItems.get(this.fetish);

        // Final result is done here, but additional computations needs to be done first
        if (sacrificedItem == null) {
            cir.setReturnValue(Result.ABORTED_REFUND);
        } else {
            Result result = InfusedSpiritEffect.tryBindFetish(world, pos, sacrificedItem.getStack(), ghosts) ? Result.COMPLETED : Result.ABORTED_REFUND;
            cir.setReturnValue(result);

            Iterator<SpiritEffectRecipe> var4 = SpiritEffectManager.INSTANCE.getEffects().iterator();

            // Unfortunately, InfusedSpiritEffect.tryBindFetish is static, and a Player parameter could not be
            // easily injected by Mixins. So we have to re-perform recipe scanning to get which recipe actually matched
            // the creatures
            SpiritEffectRecipe recipe = null;
            do {
                if (!var4.hasNext()) {
                    break;
                }

                recipe = var4.next();
            } while(!recipe.matches(ghosts));

            // Now we finally have access to both Recipe and Player who activated the ritual
            if (recipe != null && recipe.getHidden()) {
                EntityPlayer player = ritual.getInitiatingPlayer(world);
                if (player == null) return;
                IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
                if (progress == null) {
                    WitcheryCompanion.logger.warn("Could not unlock secret for InfusedSpiritEffect: progress is null");
                    return;
                }
                SpiritEffectRecipeProcessor.SpiritEffectRecipeInfo info = new SpiritEffectRecipeProcessor.SpiritEffectRecipeInfo(recipe);
                progress.unlockProgress(ProgressUtils.getSpiritEffectRecipeSecret(info.id));
                ProgressSync.serverRequest(player);
            }
        }

   }


}
