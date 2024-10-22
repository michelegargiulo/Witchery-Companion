package com.smokeythebandicoot.witcherycompanion.api.cauldron;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.CauldronRecipe;

import java.util.*;

public class CauldronApi {

    // Recipes
    public static final HashMap<ResourceLocation, CauldronRecipe> recipesToAdd = new HashMap<>();
    public static final List<ResourceLocation> recipesToRemove = new ArrayList<>();

    // Blockstate heat sources
    private static final HashSet<IBlockState> heatSourceBlockStates = new HashSet<>();

    // Block heat sources. In default Witchery, FIRE is the only heat source
    private static final HashSet<Block> heatSourceBlocks = new HashSet<>(
            Collections.singletonList(Blocks.FIRE));


    /** Register a new blockstate as heat source */
    public static void registerHeatSource(IBlockState state) {
        heatSourceBlockStates.add(state);
    }

    /** Register a new block as heat source */
    public static void registerHeatSource(Block block) {
        heatSourceBlocks.add(block);
    }

    /** Remove a blockstate as heat source */
    public static void removeHeatSource(IBlockState state) {
        heatSourceBlockStates.remove(state);
    }

    /** Remove a block as heat source */
    public static void removeHeatSource(Block block) {
        heatSourceBlocks.remove(block);
    }

    /** Returns true if the specified blockstate is an heat source */
    public static boolean isHeatSource(IBlockState state) {
        return heatSourceBlockStates.contains(state) || heatSourceBlocks.contains(state.getBlock());
    }

    public static void registerRecipe(ItemStack result, Ingredient trigger, int power, Ingredient... inputs) {
        registerRecipe(null, result, trigger, power, inputs);
    }

    public static void registerRecipe(ResourceLocation id, ItemStack result, Ingredient trigger, int power, Ingredient... inputs) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("cauldron_");

        // Retrieve inputs (max 6)
        NonNullList<CauldronRecipe.PoweredItem> inp = NonNullList.create();
        for (Ingredient input : inputs) {
            inp.add(new CauldronRecipe.PoweredItem(input, 0));
        }

        // Create the recipe and store for later addition
        CauldronRecipe recipe = new CauldronRecipe(id, null, inp,
                new CauldronRecipe.PoweredItem(trigger, power), result);
        recipesToAdd.put(id, recipe);
    }

    public static void removeRecipe(ResourceLocation id) {
        if (id != null) recipesToRemove.add(id);
    }

    /** Internal usage */
    public static Block getFireBlock() {
        return Blocks.FIRE;
    }

}
