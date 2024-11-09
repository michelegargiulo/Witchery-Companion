package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.sacrifice;

import com.smokeythebandicoot.witcherycompanion.api.accessors.rite.IItemRiteSacrificeAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.rite.effect.RiteEffect;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.RiteSacrifice;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Mixins:
 * [Feature] Accessor for Rite item sacrifices
 * [Bugfix] Fix Rite of Binding (Copy Waystone) returning bound waystone but with no bound position
 *      Credit: @MsRandom
 *      Fix: <a href="https://github.com/WitcheryResurrected/WitcheryResurrected/commit/0e2876555086761d1cac8a509a6ee528fff86167#diff-07b7eb540a59cd6d66a607a45b1d0a1fd17138a7be42824ee4bc8e159f1c053bR135">Relevant Commit</a>
 */
@Mixin(ItemRiteSacrifice.class)
public abstract class ItemRiteSacrificeMixin extends RiteSacrifice implements IItemRiteSacrificeAccessor {

    @Shadow(remap = false) @Final
    private List<ItemRiteSacrifice.ItemRequirement> requirements;

    private ItemRiteSacrificeMixin(SacrificeSerializer<?> serializer) {
        super(serializer);
    }

    @Override
    public List<ItemRiteSacrifice.ItemRequirement> getRequirements() {
        return this.requirements;
    }

    @Inject(method = "handleItem", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixCopyBinding(World world, ItemStack stack, ItemRiteSacrifice.ItemRequirement requirement, double x, double y, double z, TileEntityCircle.ActivatedRitual ritual, BlockPos pos, CallbackInfo ci) {
        if (pos == null) {
            pos = new BlockPos(x, y, z);
        }

        long identifier = requirement.getIdentifier();
        ItemStack newStack = stack.copy();
        newStack.setCount(1);

        ritual.sacrificedItems.put(identifier,
                new RiteEffect.SacrificedItem(newStack, requirement.getReplacement(), pos));

        if (requirement.getAction() != null && requirement.getAction().perform(world, stack, ritual)) {
            world.playSound(null, x, y, z, SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 0.5f, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryUtils.addNewParticles(world, EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.5);
        } else {
            stack.setCount(1);
            stack.shrink(1);
            world.playSound(null, x, y, z, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5f, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryUtils.addNewParticles(world, EnumParticleTypes.EXPLOSION_NORMAL, x, y, z, 0.5);
        }

        ci.cancel();
    }

    @Inject(method = "parseIdentifier", remap = false, cancellable = true, at = @At("HEAD"))
    private static void fixParseIdentifier(String text, CallbackInfoReturnable<Long> cir) {
        if (ModConfig.PatchesConfiguration.RitesTweaks.copyWaystone_fixResult) {
            if (text.isEmpty()) {
                cir.setReturnValue(0L);
                return;
            }
            try {
                cir.setReturnValue(Long.parseLong(text));
            } catch (Exception ignored) {
                cir.setReturnValue((long) text.hashCode());
            }
        }
    }

    /**
     * @author SmokeyTheBandicoot
     * @reason Simply broken
     */
    @Overwrite(remap = false)
    public static final long parseIdentifier(@Nonnull String text) {
        if (text.isEmpty()) return 0;
        try {
            return Long.parseLong(text);
        } catch (Exception ignored) {
            return text.hashCode();
        }
    }
}
