package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.cauldron.CauldronApi;
import com.smokeythebandicoot.witcherycompanion.api.kettle.KettleApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Kettle")
@ZenRegister
public class KettleHandler {

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as an IBlockState. Must be placed immediately below the kettle")
    public static void registerHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        KettleApi.registerHeatSource(CraftTweakerMC.getBlockState(blockstate));
    }

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as a Block. Must be placed immediately below the kettle")
    public static void registerHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        KettleApi.registerHeatSource(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        KettleApi.removeHeatSource(CraftTweakerMC.getBlockState(blockstate));
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        KettleApi.removeHeatSource(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the specified IBlockState is a valid Cauldron Heat Source")
    public static boolean isHeatSource(crafttweaker.api.block.IBlockState state) {
        return KettleApi.isHeatSource(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Registers a new recipe to the Kettle. Refer to Witchery: Companion wiki on Github for details")
    public static void registerRecipe(IItemStack result, float requiredPower, int hatBonus, String familiarPower, Integer dimension, boolean isSpecial, IIngredient... inputs) {
        KettleApi.registerRecipe(
                null,
                CraftTweakerMC.getItemStack(result),
                requiredPower,
                hatBonus,
                familiarPower,
                dimension,
                isSpecial,
                Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).toArray(Ingredient[]::new)
                );
    }

    @ZenMethod
    @ZenDoc(value="Removes a Kettle Recipe")
    public static void removeRecipe(String resourceLocation) {
        KettleApi.removeRecipe(new ResourceLocation(resourceLocation));
    }

}
