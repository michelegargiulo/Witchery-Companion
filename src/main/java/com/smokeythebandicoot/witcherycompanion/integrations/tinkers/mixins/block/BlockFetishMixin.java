package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Hegemony;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.block.BlockFetish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: the Tinkers Boline counts as the Witchery Boline on the Fetish
 */
@Mixin(BlockFetish.class)
public abstract class BlockFetishMixin {

    /** Fetish activation (held item vs Boline) **/
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", ordinal = 3,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item hegemonyFetishBoline(ItemStack instance, Operation<Item> original) {
        return Hegemony.resolveAs(instance, original.call(instance), WitcheryGeneralItems.BOLINE);
    }
}
