package com.smokeythebandicoot.witcherycompanion.integrations.conarm.mixins.item;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.conarm.ConArmIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemPolynesiaCharm;
import net.msrandom.witchery.item.ItemWitchesClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemPolynesiaCharm.class)
public abstract class ItemPolynesiaCharmMixin {

    @WrapOperation(method = "onItemRightClick", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isRobeWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean checkCreeperRepellentTrait(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (TinkersUtils.hasEffect(stack, ConArmIntegration.MODIFIER_CREEPER_REPELLENT)) {
            return true;
        }
        return original.call(instance, player);
    }

    @WrapOperation(method = "onItemRightClick", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isRobeWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean checkUndeadRepellentTrait(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (TinkersUtils.hasEffect(stack, ConArmIntegration.MODIFIER_UNDEAD_REPELLENT)) {
            return true;
        }
        return original.call(instance, player);
    }

}
