package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.effect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.rite.effect.RiteEffectCurseCreature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Strength of the curse
 * [Bugfix] Full coven bonus only with exactly 6 witches
 */
@Mixin(RiteEffectCurseCreature.class)
public class RiteEffectCurseCreatureMixin {

    /**
     * Witchery gives the full coven bonus when the coven size is exactly 6, and the smaller one from 3 witches on.
     * With a bigger coven (maximum coven size raised) the rite would get the smaller bonus. The size is capped to 6
     * where it is read; the method only uses the size for these two comparisons
     */
    @ModifyExpressionValue(method = "process", remap = false, at = @At(value = "FIELD", remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityCircle$ActivatedRitual;covenSize:I"))
    private int witcherycompanion$capCovenSizeForBonus(int covenSize) {
        if (ModConfig.PatchesConfiguration.RitesTweaks.curseCreature_fixLargeCovenBonus) {
            return Math.min(covenSize, 6);
        }
        return covenSize;
    }

    @WrapOperation(method = "process", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/item/WitcheryPoppetItems;voodooProtectionActivated(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;I)Z"))
    public boolean strengthTweak(EntityPlayer allowLightning, ItemStack i, EntityLivingBase attackingEntity, int strength, Operation<Boolean> original) {
        return original.call(allowLightning, i, attackingEntity, strength * ModConfig.PatchesConfiguration.RitesTweaks.curseCreature_tweakStrength);
    }

}
