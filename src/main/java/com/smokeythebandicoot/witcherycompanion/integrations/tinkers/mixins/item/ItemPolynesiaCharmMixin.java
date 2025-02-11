package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.item;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemPolynesiaCharm;
import net.msrandom.witchery.item.ItemWitchesClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mixin(ItemPolynesiaCharm.class)
public abstract class ItemPolynesiaCharmMixin {

    @WrapOperation(method = "onItemRightClick", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isRobeWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean checkCreeperRepellentTrait(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (TinkerUtil.hasModifier(stack.getTagCompound(), Integration.MODIFIER_CREEPER_REPELLENT.identifier)) {
            return true;
        }
        return original.call(instance, player);
    }

    @WrapOperation(method = "onItemRightClick", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/msrandom/witchery/item/ItemWitchesClothes;isRobeWorn(Lnet/minecraft/entity/player/EntityPlayer;)Z"))
    private boolean checkUndeadRepellentTrait(ItemWitchesClothes instance, EntityPlayer player, Operation<Boolean> original) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (TinkerUtil.hasModifier(stack.getTagCompound(), Integration.MODIFIER_UNDEAD_REPELLENT.identifier)) {
            return true;
        }
        return original.call(instance, player);
    }

}
