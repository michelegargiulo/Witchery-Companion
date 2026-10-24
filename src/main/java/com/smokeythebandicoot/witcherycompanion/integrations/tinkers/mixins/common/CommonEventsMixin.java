package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: the Tinkers Arthana counts as the Witchery Arthana when a creature dies (more drops)
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** Arthana in the main hand when a creature dies (onLivingDeath, held item vs Arthana) **/
    @WrapOperation(method = "onLivingDeath", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item hegemonyArthanaMainHand(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.getArthana());
    }

    /** Arthana in the off hand when a creature dies (onLivingDeath, held item vs Arthana) **/
    @WrapOperation(method = "onLivingDeath", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item hegemonyArthanaOffHand(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.getArthana());
    }
}
