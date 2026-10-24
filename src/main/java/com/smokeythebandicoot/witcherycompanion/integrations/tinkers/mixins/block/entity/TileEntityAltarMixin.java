package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Hegemony;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: the Tinkers Arthana counts as the Witchery Arthana on the Altar
 */
@Mixin(TileEntityAltar.class)
public abstract class TileEntityAltarMixin {

    /** Altar artifacts (placed item vs Arthana). The Altar counts the Arthana only once with a flag,
     * so a Witchery Arthana and a Tinkers one on the same Altar do not stack **/
    @WrapOperation(method = "updateArtifacts", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item hegemonyAltarArthana(ItemStack instance, Operation<Item> original) {
        return Hegemony.resolveAs(instance, original.call(instance), WitcheryGeneralItems.getArthana());
    }
}
