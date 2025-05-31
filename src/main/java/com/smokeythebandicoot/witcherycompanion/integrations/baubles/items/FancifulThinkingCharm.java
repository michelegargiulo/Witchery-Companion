package com.smokeythebandicoot.witcherycompanion.integrations.baubles.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Fanciful Thinking Charm is just a regular item that gets checked where appropriate.
 * This class makes it a Bauble if it is registered in WitcheryIngredientItems (name: DISRUPTED_DREAMS_CHARM)
 */
public class FancifulThinkingCharm extends Item implements IBauble {

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

}
