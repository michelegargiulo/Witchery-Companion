package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.brazier.BrazierEffectRecipe;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import net.msrandom.witchery.recipe.brazier.BrazierSummoningRecipe;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ParametersAreNonnullByDefault
public class BrazierApi {

    // New recipes that are added through crafttweaker, in addition to Witchery ones
    public static final HashMap<ResourceLocation, BrazierRecipe> recipesToAdd = new HashMap<>();
    public static final List<ResourceLocation> recipesToRemove = new ArrayList<>();


    public static void registerSummoningRecipe(Ingredient input1, Ingredient input2, Ingredient input3, int burnTime, boolean needsPower, EntityType<EntityCreature> summon, EntityType<EntityCreature> extra) {
        registerSummoningRecipe(null, input1, input2, input3, burnTime, needsPower, summon, extra);
    }

    public static void registerSummoningRecipe(@Nullable ResourceLocation id, Ingredient input1, Ingredient input2, Ingredient input3, int burnTime, boolean needsPower, EntityType<EntityCreature> summon, EntityType<EntityCreature> extra) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("braziersummon_");

        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);

        BrazierSummoningRecipe recipe = new BrazierSummoningRecipe(id, inputs,
                burnTime, needsPower, false, summon, extra);
        recipesToAdd.put(id, recipe);
    }

    public static void registerEffectRecipe(Ingredient input1, Ingredient input2, Ingredient input3, int burnTime, boolean needsPower, Potion potion, int radius) {
        registerEffectRecipe(null, input1, input2, input3, burnTime, needsPower, potion, radius);
    }

    public static void registerEffectRecipe(@Nullable ResourceLocation id, Ingredient input1, Ingredient input2, Ingredient input3, int burnTime, boolean needsPower, Potion potion, int radius) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("braziereffect_");

        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.add(input1);
        inputs.add(input2);
        inputs.add(input3);

        BrazierEffectRecipe recipe = new BrazierEffectRecipe(id, inputs, burnTime, needsPower, false, potion, radius);
        recipesToAdd.put(id, recipe);
    }

    public static void removeRecipe(ResourceLocation id) {
        recipesToRemove.add(id);
    }

}
