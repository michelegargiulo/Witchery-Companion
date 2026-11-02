package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.CommonTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.TransformationTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentTranslation;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.init.WitcheryPotionEffects;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Bugfix] Fix ResizingPotion not working on Players
 [Bugfix] Vampires lose all their hunger bar when they eat food, like in the original Witchery. W:R does it in a
    LivingUpdateEvent handler that checks prevFoodLevel < foodLevel, but in 1.12 that event fires before the active
    item is finished (updateActiveHand, right below the event) and FoodStats.onUpdate syncs prevFoodLevel at the end
    of the same tick, so the handler never sees the food gain. Here the hunger bar is compared before and after
    updateActiveHand, so only the food gained by finishing an item is drained, and not the hunger that W:R
    restores from the Vampire blood pool in the LivingUpdateEvent handler
 */
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {

    @Shadow(remap = true)
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow(remap = true)
    protected int ticksElytraFlying;

    /** This Mixin makes it so that players cannot use Elytra when resized */
    @WrapOperation(method = "updateElytra", remap = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;getFlag(I)Z", remap = true))
    private boolean disableElytraWhenResized(EntityLivingBase instance, int flag, Operation<Boolean> original) {
        if (this.ticksElytraFlying > 1 && CommonTweaks.tweak_disallowElytraWhenTransformedOrResized) {
            if ((Object)this instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) (Object) this;

                if (this.isPotionActive(WitcheryPotionEffects.RESIZING)) {
                    player.sendStatusMessage(new TextComponentTranslation(
                            "witcherycompanion.message.potion.resizing.elytra_disallow",
                            new Object[0]), true);
                    return false;
                }

                PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
                if (playerEx.getCurrentForm() != null) {
                    player.sendStatusMessage(new TextComponentTranslation(
                            "witcherycompanion.message.transform.elytra_disallow",
                            new Object[0]), true);
                    return false;
                }
            }
        }
        return original.call(instance, flag);
    }

    /** Items are finished (and food is eaten) in updateActiveHand, which runs after the LivingUpdateEvent. The hunger
     * bar is checked only around this call: this way hunger restored by W:R from the blood pool (done in the
     * LivingUpdateEvent handler, earlier in the tick) is never drained. Server-side only. isTransformation() does not
     * mutate state, unlike getTransformation() */
    @WrapOperation(method = "onUpdate", remap = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/entity/EntityLivingBase;updateActiveHand()V"))
    private void witcherycompanion$drainVampireHungerOnEating(EntityLivingBase instance, Operation<Void> original) {
        if (!TransformationTweaks.vampire_fixFoodDrain ||
                !(instance instanceof EntityPlayer) || instance.world.isRemote) {
            original.call(instance);
            return;
        }

        EntityPlayer player = (EntityPlayer) instance;
        FoodStats foodStats = player.getFoodStats();
        int foodBefore = foodStats.getFoodLevel();
        original.call(instance);

        int foodAfter = foodStats.getFoodLevel();
        if (foodAfter > foodBefore &&
                WitcheryUtils.getExtension(player).isTransformation(WitcheryCreatureTraits.VAMPIRE)) {
            foodStats.addStats(-foodAfter, 0.0f);
        }
    }


}
