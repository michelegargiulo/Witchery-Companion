package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.cauldron;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ITileEntityCauldronAccessor {

    float witcherycompanion$accessor$getNeededPower();

    /** Returns every stack that can currently satisfy a recipe INPUT: brewing ingredients and non-brewing
     * ingredients, excluding the last added stack (the recipe trigger). The returned list is a fresh copy */
    List<ItemStack> witcherycompanion$accessor$getCraftingPool();

}
