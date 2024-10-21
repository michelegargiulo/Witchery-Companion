package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.mutandis.MutandisApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Mutandis")
@ZenRegister
public class MutandisHandler {

    /** ========== GRASS CONVERSION ========== **/

    @ZenMethod
    @ZenDoc(value="Registers a new Grass Conversion for Mutandis")
    public static void addGrassConversion(IBlockState sourceState, IBlockState targetState) {
        MutandisApi.addGrassConversion(CraftTweakerMC.getBlockState(sourceState), CraftTweakerMC.getBlockState(targetState));
    }


    @ZenMethod
    @ZenDoc(value="Removes a Grass Conversion for Mutandis")
    public static void removeGrassConversion(IBlockState sourceState) {
        MutandisApi.removeGrassConversion(CraftTweakerMC.getBlockState(sourceState));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the block can be converted into another with Mutandis (extremis is always needed)")
    public static boolean isGrassConvertible(IBlockState state) {
        return MutandisApi.isGrassConvertible(CraftTweakerMC.getBlockState(state));
    }




    /** ========== CLAY CONVERSION ========== **/

    @ZenMethod
    @ZenDoc(value="Registers a new Clay Conversion for Mutandis")
    public static void addClayConversion(IBlockState sourceState, IBlockState targetState) {
        MutandisApi.addClayConversion(CraftTweakerMC.getBlockState(sourceState), CraftTweakerMC.getBlockState(targetState));
    }

    @ZenMethod
    @ZenDoc(value="Removes a Clay Conversion for Mutandis")
    public static void removeClayConversion(IBlockState sourceState) {
        MutandisApi.removeClayConversion(CraftTweakerMC.getBlockState(sourceState));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the block can be converted into another with Mutandis when Water is on top (extremis is always needed)")
    public static boolean isClayConvertible(IBlockState state) {
        return MutandisApi.isClayConvertible(CraftTweakerMC.getBlockState(state));
    }



    /** ========== CLAY CONVERSION ========== **/

    @ZenMethod
    @ZenDoc(value="Registers a new Plant mutation for Mutandis")
    public static void setMutandisConversion(IBlockState state, boolean needsExtremis) {
        MutandisApi.setMutandisConversion(CraftTweakerMC.getBlockState(state), needsExtremis);
    }

    @ZenMethod
    @ZenDoc(value="Removes a Plant mutation for Mutandis")
    public static void removeMutandisConversion(IBlockState state) {
        MutandisApi.removeMutandisConversion(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the block can be converted into another with Mutandis")
    public static boolean hasConversion(IBlockState state, boolean extremisAvailable) {
        return MutandisApi.hasConversion(CraftTweakerMC.getBlockState(state), extremisAvailable);
    }

}
