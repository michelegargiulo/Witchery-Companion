package com.smokeythebandicoot.witcherycompanion.api.distillery;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.DistilleryRecipe;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ParametersAreNonnullByDefault
public class DistilleryApi {

    // New recipes that are added through crafttweaker, in addition to Witchery ones
    public static final HashMap<ResourceLocation, DistilleryRecipe> recipesToAdd = new HashMap<>();
    public static final List<ResourceLocation> recipesToRemove = new ArrayList<>();


    public static void registerRecipe(Ingredient input1, Ingredient input2, int clayJars, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4) {
        registerRecipe(null, input1, input2, clayJars, output1, output2, output3, output4);
    }

    public static void registerRecipe(@Nullable ResourceLocation id, Ingredient input1, Ingredient input2, int clayJars, ItemStack output1, ItemStack output2, ItemStack output3, ItemStack output4) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("distillery_");

        NonNullList<ItemStack> outputs = NonNullList.create();
        outputs.add(output1);
        outputs.add(output2);
        outputs.add(output3);
        outputs.add(output4);

        // Create the recipe and store for later addition
        DistilleryRecipe recipe = new DistilleryRecipe(id, input1, input2, clayJars, outputs);
        recipesToAdd.put(id, recipe);
    }

    public static void removeRecipe(ResourceLocation id) {
        recipesToRemove.add(id);
    }

}
