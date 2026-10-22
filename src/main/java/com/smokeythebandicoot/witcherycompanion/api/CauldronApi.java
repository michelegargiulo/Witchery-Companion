package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.utils.IngredientMatcher;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.CauldronRecipe;
import net.msrandom.witchery.util.WitcheryUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
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

    public static void registerRecipe(@Nullable ResourceLocation id, ItemStack result, Ingredient trigger, int power, Ingredient... inputs) {
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
        recipesToRemove.add(id);
    }

    /** Returns true if the stack is a valid ingredient (trigger or input) of at least one Cauldron recipe that does
     * NOT come from Witchery itself (Crafttweaker, other mods' data folders, ...). Internal usage */
    public static boolean isCustomRecipeIngredient(@Nullable World world, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (CauldronRecipe recipe : WitcheryUtils.getRecipeManager(world).getRecipesForType(WitcheryRecipeTypes.CAULDRON)) {
            if (!"witchery".equals(recipe.getId().getNamespace()) && recipe.isItemValid(stack)) return true;
        }
        return false;
    }

    /** Returns true if every input can be paired with a different stack of the pool. Internal usage */
    public static boolean areInputsSatisfied(List<CauldronRecipe.PoweredItem> inputs, List<ItemStack> pool) {
        return IngredientMatcher.matchesAll(inputs, pool, (input, stack) -> input.getIngredient().apply(stack));
    }

    /** Internal usage */
    public static Block getFireBlock() {
        return Blocks.FIRE;
    }

}
