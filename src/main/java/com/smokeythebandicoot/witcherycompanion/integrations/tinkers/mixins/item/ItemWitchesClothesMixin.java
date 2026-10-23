package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.item.ItemWitchesClothes;
import net.msrandom.witchery.item.traits.Invisible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Homing TiC Modifier
 * [Feature] Barkable TiC Trait and Barked TiC Modifier increase the Bark Belt max charges
 */
@Mixin(ItemWitchesClothes.class)
public abstract class ItemWitchesClothesMixin extends ItemArmor implements Invisible {

    private ItemWitchesClothesMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    /** This Mixin tricks witchery into thinking that TiC boots with Homing modifier are Witchery's Ruby Slippers **/
    @WrapOperation(method = "noPlaceLikeHome", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item handleHomingModifier(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, Integration.MODIFIER_HOMING)) {
            return WitcheryEquipmentItems.RUBY_SLIPPERS;
        }
        return original.call(instance);
    }

    /** This Mixin adds to the Bark Belt max charges the ones of TiC armor with the Barkable trait or the Barked
     * modifier. Witchery's own pieces are already counted by the original method, so they are skipped here **/
    @Inject(method = "getMaxChargeLevel", remap = false, cancellable = true, at = @At("RETURN"))
    private static void maxChargeLevelIncludesTinkersArmor(EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        int level = cir.getReturnValue();
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] {
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET,
        }) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ItemWitchesClothes) continue;
            if (TinkersUtils.hasEffect(stack, Integration.TRAIT_BARKABLE) ||
                    TinkersUtils.hasEffect(stack, Integration.MODIFIER_BARKED)) {
                level += 2;
            }
        }
        cir.setReturnValue(level);
    }
}
