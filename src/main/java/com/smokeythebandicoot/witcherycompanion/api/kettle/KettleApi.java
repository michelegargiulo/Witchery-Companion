package com.smokeythebandicoot.witcherycompanion.api.kettle;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.msrandom.witchery.WitcheryResurrected;
import net.msrandom.witchery.recipe.KettleRecipe;

import java.util.*;

public class KettleApi {

    // New recipes that are added through crafttweaker, in addition to Witchery ones
    public static final HashMap<ResourceLocation, KettleRecipe> recipesToAdd = new HashMap<>();
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


    public static void registerRecipe(ItemStack result, float power, Ingredient... inputs) {
        registerRecipe(null, result, power, 0, null, null, false, inputs);
    }

    public static void registerRecipe(ItemStack result, float power, int hatBonus, String familiarPower, Integer dimension, boolean special, Ingredient... inputs) {
        registerRecipe(null, result, power, hatBonus, familiarPower, dimension, special, inputs);
    }

    public static void registerRecipe(ResourceLocation id, ItemStack result, float power, int hatBonus, String familiarPower, Integer dimension, boolean special, Ingredient... inputs) {
        // Generate random id if one is not provided
        if (id == null) id = Utils.generateRandomRecipeId("kettle_");

        // Retrieve familiar power
        ResourceLocation familiar = null;
        if (familiarPower != null && Arrays.stream(EFamiliarPower.values()).anyMatch(f -> f.name().equalsIgnoreCase(familiarPower))) {
            familiar = EFamiliarPower.valueOf(familiarPower.toUpperCase()).familiarPowerId;
        }

        // Retrieve dimension
        DimensionType dimensionType = null;
        if (dimension != null) {
            dimensionType = DimensionManager.getProviderType(dimension);
        }

        // Retrieve inputs (max 6)
        NonNullList<Ingredient> inp = NonNullList.create();
        inp.addAll(Arrays.asList(inputs).subList(0, 6)); // Max 6 inputs

        // Create the recipe and store for later addition
        KettleRecipe recipe = new KettleRecipe(id, result, inp, hatBonus, familiar, power, dimensionType, special);
        recipesToAdd.put(id, recipe);
    }

    public static void removeRecipe(ResourceLocation id) {
        if (id != null) recipesToRemove.add(id);
    }



    /** Internal usage */
    public static Material getFireMaterial() {
        return Material.FIRE;
    }

    public enum EFamiliarPower {
        BROOM_MASTERY("broom_mastery"),
        CURSE_MASTERY("curse_mastery"),
        BREW_MASTERY("brew_mastery"),
        ;

        public final ResourceLocation familiarPowerId;

        EFamiliarPower(String path) {
            this.familiarPowerId = new ResourceLocation(WitcheryResurrected.MOD_ID, path);
        }
    }
}
