package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.ECompanionTrait;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Feature] Witch Clothing trait makes players being ignored by creepers
 * [Feature] Necromancer Robes trait makes players being ignored by undeads
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** Witch Clothing trait makes players being ignored by creepers  **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item witchClothingTraitIgnoreCreeper(ItemStack instance, Operation<Item> original) {
        if (Integration.hasTrait(instance, ECompanionTrait.WITCH_CLOTHING)) {
            return WitcheryEquipmentItems.WITCH_ROBES;
        }
        return original.call(instance);
    }

    /** Necromancer Robes trait makes players being ignored by undeads **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1))
    private static Item necromancerClothingTraitIgnoreCreeper(ItemStack instance, Operation<Item> original) {
        if (Integration.hasTrait(instance, ECompanionTrait.NECROMANCER_CLOTHING)) {
            return WitcheryEquipmentItems.NECROMANCERS_ROBES;
        }
        return original.call(instance);
    }

}
