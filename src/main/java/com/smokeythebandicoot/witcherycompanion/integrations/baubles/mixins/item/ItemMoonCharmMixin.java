package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemMoonCharm;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixins:
 * [Tweak] Make Moon Charm a Bauble (charm type)
 */
@Mixin(ItemMoonCharm.class)
public abstract class ItemMoonCharmMixin extends Item implements IBauble {

    /** Implements the only abstract method **/
    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }
}
