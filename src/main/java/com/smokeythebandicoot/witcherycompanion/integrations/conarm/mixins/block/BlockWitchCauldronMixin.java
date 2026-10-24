package com.smokeythebandicoot.witcherycompanion.integrations.conarm.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.conarm.ConArmIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.item.ItemWitchesClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Feature] Witch Clothing trait compat
 * [Feature] Baba's bless trait compat
 * [Feature] Necromantic trait compat
 */
@Mixin(BlockWitchCauldron.class)
public abstract class BlockWitchCauldronMixin {

    /** This Mixin implements the Witch Clothing trait to emulate Witch Hat **/
    @WrapOperation(method = "getDrainAmount", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isHatWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean traitBrewAffinityWitchHat(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (TinkersUtils.hasEffect(stack, ConArmIntegration.TRAIT_BREW_AFFINITY)) {
            return true;
        }
        return original.call(instance, player);
    }

    /** This Mixin implements the Baba's Bless trait to emulate Baba's Hat **/
    @WrapOperation(method = "getDrainAmount", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isHatWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean traitBabasBlessBabaHat(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (TinkersUtils.hasEffect(stack, ConArmIntegration.MODIFIER_BABAS_BLESS)) {
            return true;
        }
        return original.call(instance, player);
    }

    /** This Mixin implements the Witch Clothing trait to emulate Witch Robe. There's no Mixin for the second 'isRobeWorn' as
     * they are mixed into the same Trait **/
    @WrapOperation(method = "getDrainAmount", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isRobeWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean traitBrewAffinityWitchRobe(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (TinkersUtils.hasEffect(stack, ConArmIntegration.TRAIT_BREW_AFFINITY)) {
            return true;
        }
        return original.call(instance, player);
    }


}
