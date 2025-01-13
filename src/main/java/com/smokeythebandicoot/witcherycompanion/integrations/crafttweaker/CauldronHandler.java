package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.CauldronApi;
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

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Cauldron")
@ZenRegister
public class CauldronHandler {

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as an IBlockState. Must be placed immediately below the cauldron")
    public static void registerHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        CauldronApi.registerHeatSource(CraftTweakerMC.getBlockState(blockstate));
    }

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as a Block. Must be placed immediately below the cauldron")
    public static void registerHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        CauldronApi.registerHeatSource(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        CauldronApi.removeHeatSource(CraftTweakerMC.getBlockState(blockstate));
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        CauldronApi.removeHeatSource(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the specified IBlockState is a valid Cauldron Heat Source")
    public static boolean isHeatSource(crafttweaker.api.block.IBlockState state) {
        return CauldronApi.isHeatSource(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Registers a new Cauldron recipe")
    public static void registerRecipe(IItemStack result, IIngredient trigger, int power, IIngredient[] inputs) {

        if (result == null || trigger == null || inputs.length == 0) return;

        Ingredient[] inp = new Ingredient[inputs.length];
        for (int i = 0; i < inp.length; i++) {
            inp[i] = CraftTweakerMC.getIngredient(inputs[i]);
        }

        CauldronApi.registerRecipe(null,
                CraftTweakerMC.getItemStack(result),
                CraftTweakerMC.getIngredient(trigger),
                power,
                inp
        );
    }

    @ZenMethod
    @ZenDoc(value="Removes a Cauldron Recipe")
    public static void removeRecipe(String resourceLocation) {
        CauldronApi.removeRecipe(new ResourceLocation(resourceLocation));
    }

}
