package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.api.recipes.IRecipeManagerAccessor;
import net.minecraft.inventory.IInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resources.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.recipe.WitcheryRecipe;
import net.msrandom.witchery.util.WitcheryUtils;

public class ContentUtils {

    public static boolean isSymbolEffectEnabled(String effectId) {
        return SymbolEffect.REGISTRY.get(new ResourceLocation("witchery:" + effectId)) != null;
    }

    @SuppressWarnings({"unchecked"})
    public static WitcheryRecipe getRecipeForType(RecipeType recipeType, ResourceLocation recipeId) {
        RecipeManager recipeManager = WitcheryUtils.getRecipeManager(null);
        if ((Object)recipeManager instanceof IRecipeManagerAccessor) {
            IRecipeManagerAccessor accessor = (IRecipeManagerAccessor) (Object) recipeManager;
            return accessor.getRecipeForType(recipeType, recipeId);
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends WitcheryRecipe> T getRecipeForType(RecipeType recipeType, String recipeId) {
        RecipeManager recipeManager = WitcheryUtils.getRecipeManager(null);
        if ((Object)recipeManager instanceof IRecipeManagerAccessor) {
            IRecipeManagerAccessor accessor = (IRecipeManagerAccessor) (Object) recipeManager;
            return (T)accessor.getRecipeForType(recipeType, new ResourceLocation(recipeId));
        }
        return null;
    }


}
