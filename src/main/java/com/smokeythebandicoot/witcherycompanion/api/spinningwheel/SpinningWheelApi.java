package com.smokeythebandicoot.witcherycompanion.api.spinningwheel;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.SpinningWheelRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpinningWheelApi {

    // New recipes that are added through crafttweaker, in addition to Witchery ones
    public static final HashMap<ResourceLocation, SpinningWheelRecipe> recipesToAdd = new HashMap<>();
    public static final List<ResourceLocation> recipesToRemove = new ArrayList<>();


    public static void registerRecipe(ItemStack result, Ingredient fibre, int fibreCount, Ingredient modifier1, Ingredient modifier2, Ingredient modifier3) {
        registerRecipe(null, result, fibre, fibreCount, modifier1, modifier2, modifier3);
    }

    public static void registerRecipe(ResourceLocation id, ItemStack result, Ingredient fibre, int fibreCount, Ingredient modifier1, Ingredient modifier2, Ingredient modifier3) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("spinningwheel_");

        NonNullList<Ingredient> modifiers = NonNullList.create();
        modifiers.add(modifier1);
        modifiers.add(modifier2);
        modifiers.add(modifier3);



        // Create the recipe and store for later addition
        SpinningWheelRecipe recipe = new SpinningWheelRecipe(id, result, fibre, fibreCount, modifiers);
        recipesToAdd.put(id, recipe);
    }

    public static void removeRecipe(ResourceLocation id) {
        if (id != null) recipesToRemove.add(id);
    }
}
