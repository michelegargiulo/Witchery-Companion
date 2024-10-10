package com.smokeythebandicoot.witcherycompanion.api.recipes;

import net.minecraft.item.ItemStack;

public interface IIngredientAccessor {

    ItemStack[] getAllMatchingStacks();

}
