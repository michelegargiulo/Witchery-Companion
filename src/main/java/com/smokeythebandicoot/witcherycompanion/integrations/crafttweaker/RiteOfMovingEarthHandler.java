package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.RiteOfMovingEarthApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.RiteMovingEarth")
@ZenRegister
public class RiteOfMovingEarthHandler {

    @ZenMethod
    @ZenDoc(value="Blacklists the block from being moved")
    public static void blacklistBlock(IBlock block) {
        RiteOfMovingEarthApi.blacklistBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Blacklists the blockstate from being moved")
    public static void blacklistBlockState(IBlockState state) {
        RiteOfMovingEarthApi.blacklistBlockState(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Allows the block to be moved")
    public static void allowBlock(IBlock block) {
        RiteOfMovingEarthApi.allowBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Allows the blockstate to be moved")
    public static void allowBlockState(IBlockState state) {
        RiteOfMovingEarthApi.allowBlockState(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Checks if the blockstate can be moved")
    public static boolean canBeMoved(IBlockState state) {
        return RiteOfMovingEarthApi.canBeMoved(CraftTweakerMC.getBlockState(state));
    }

}
