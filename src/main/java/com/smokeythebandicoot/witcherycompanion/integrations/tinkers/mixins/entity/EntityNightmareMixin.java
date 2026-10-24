package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.entity.EntityNightmare;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: a Battlesign with the Huntsman modifier counts as the Spear of the Huntsman against the Nightmare
 */
@Mixin(EntityNightmare.class)
public abstract class EntityNightmareMixin {

    /** Attacker's main hand vs Spear of the Huntsman: the Nightmare stays defended for less time after being hit **/
    @WrapOperation(method = "attackEntityFrom", remap = true, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item hegemonyNightmareSpear(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.HUNTSMANS_SPEAR);
    }
}
