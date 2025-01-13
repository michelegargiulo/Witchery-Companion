package com.smokeythebandicoot.witcherycompanion.api.accessors.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.WitcheryRecipe;

public interface IRecipeManagerAccessor {

    WitcheryRecipe<IInventory> getRecipeForType(RecipeType<WitcheryRecipe<IInventory>> recipeType, ResourceLocation id);

}
