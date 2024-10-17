package com.smokeythebandicoot.witcherycompanion.mixins.minecraft.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.smokeythebandicoot.witcherycompanion.api.kettle.KettleApi;
import com.smokeythebandicoot.witcherycompanion.api.recipes.IRecipeManagerAccessor;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resources.JsonReloadListener;
import net.minecraft.resources.RecipeManager;
import net.minecraft.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.KettleRecipe;
import net.msrandom.witchery.recipe.WitcheryRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Mixins:
 * [Feature] Accessor to retrieve recipes of Type, given resource location (for example Kettle recipe with given resource location id)
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin extends JsonReloadListener implements IRecipeManagerAccessor {

    @Shadow(remap = false)
    protected abstract <C extends IInventory, T extends WitcheryRecipe<C>> Map<ResourceLocation, WitcheryRecipe<C>> getRecipes(RecipeType<T> recipeType);

    @Shadow(remap = false) @Final
    private HashMap<RecipeType<?>, Map<ResourceLocation, WitcheryRecipe<?>>> recipes;

    private RecipeManagerMixin(Gson gson, String folder) {
        super(gson, folder);
    }

    /** This Mixin implements the accessor function to retrieve a recipe given a Type and an ID **/
    @Override
    public WitcheryRecipe<IInventory> getRecipeForType(RecipeType<WitcheryRecipe<IInventory>> recipeType, ResourceLocation id) {
        Map<ResourceLocation, WitcheryRecipe<IInventory>> recipeMap = this.getRecipes(recipeType);
        return recipeMap.getOrDefault(id, null);
    }

    /** This Mixin injects at the end of the apply method, when recipes are actually reloaded, and injects recipes coming from the APIs **/
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false, at = @At(value = "INVOKE",
            target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private void injectRecipesOnApply(Map<ResourceLocation, ? extends JsonElement> value, ResourceManager resourceManager, CallbackInfo ci) {

        Map<ResourceLocation, WitcheryRecipe<?>> kettleRecipes = this.recipes.get(WitcheryRecipeTypes.KETTLE);
        kettleRecipes.putAll(KettleApi.recipesToAdd);
        for (ResourceLocation resourceLocation : KettleApi.recipesToRemove) {
            kettleRecipes.remove(resourceLocation);
        }

        this.recipes.size();

    }

}
