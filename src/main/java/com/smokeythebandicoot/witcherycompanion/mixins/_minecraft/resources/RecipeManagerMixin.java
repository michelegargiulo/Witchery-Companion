package com.smokeythebandicoot.witcherycompanion.mixins._minecraft.resources;

import com.google.gson.Gson;
import com.smokeythebandicoot.witcherycompanion.api.recipes.IRecipeManagerAccessor;
import net.minecraft.inventory.IInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resources.JsonReloadListener;
import net.minecraft.resources.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.WitcheryRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin extends JsonReloadListener implements IRecipeManagerAccessor {

    @Shadow(remap = false)
    protected abstract <C extends IInventory, T extends WitcheryRecipe<C>> Map<ResourceLocation, WitcheryRecipe<C>> getRecipes(RecipeType<T> recipeType);

    private RecipeManagerMixin(Gson gson, String folder) {
        super(gson, folder);
    }

    @Override
    public WitcheryRecipe<IInventory> getRecipeForType(RecipeType<WitcheryRecipe<IInventory>> recipeType, ResourceLocation id) {
        Map<ResourceLocation, WitcheryRecipe<IInventory>> recipeMap = this.getRecipes(recipeType);
        return recipeMap.getOrDefault(id, null);
    }

}
