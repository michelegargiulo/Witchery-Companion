package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.item.ItemGlassGoblet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: the Tinkers Boline counts as the Witchery Boline when filling the Glass Goblet
 */
@Mixin(ItemGlassGoblet.class)
public abstract class ItemGlassGobletMixin {

    /** Goblet blood collection on creature death (held item vs Boline) **/
    @WrapOperation(method = "handleCreatureDeath", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item hegemonyGobletBoline(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.BOLINE);
    }
}
