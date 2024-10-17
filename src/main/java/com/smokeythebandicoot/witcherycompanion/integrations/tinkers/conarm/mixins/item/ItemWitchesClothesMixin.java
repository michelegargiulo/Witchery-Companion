package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.item;

import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.ECompanionTrait;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemWitchesClothes;
import net.msrandom.witchery.item.traits.Invisible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Add compat for Witch Clothing tcon traits
 */
@Mixin(ItemWitchesClothes.class)
public abstract class ItemWitchesClothesMixin extends ItemArmor implements Invisible {

    private ItemWitchesClothesMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Inject(method = "isHatWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitHat(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack headStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        cir.setReturnValue(
                headStack.getItem() instanceof ItemWitchesClothes ||
                Integration.hasTrait(headStack, ECompanionTrait.WITCH_CLOTHING) ||
                Integration.hasTrait(headStack, ECompanionTrait.NECROMANCER_CLOTHING)
        );
    }

    @Inject(method = "isRobeWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitRobes(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        cir.setReturnValue(
                chestStack.getItem() instanceof ItemWitchesClothes ||
                Integration.hasTrait(chestStack, ECompanionTrait.WITCH_CLOTHING) ||
                Integration.hasTrait(chestStack, ECompanionTrait.NECROMANCER_CLOTHING)
        );
    }

    @Inject(method = "isBeltWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitBelt(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack legsStack = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        cir.setReturnValue(
                legsStack.getItem() instanceof ItemWitchesClothes ||
                Integration.hasTrait(legsStack, ECompanionTrait.WITCH_CLOTHING) ||
                Integration.hasTrait(legsStack, ECompanionTrait.NECROMANCER_CLOTHING)
        );
    }


}
