package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes;

import com.smokeythebandicoot.witcherycompanion.api.erosionbrew.ErosionBrewApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.BrewOfErosion")
@ZenRegister
public class ErosionBrewHandler {

    @ZenMethod
    @ZenDoc(value="Returns true if the current list of mineable blocks is a Whitelist")
    public static boolean isMineWhiteList() {
        return ErosionBrewApi.isMineWhiteList();
    }

    @ZenMethod
    @ZenDoc(value="Used to invert the condition of the mineable blocks list. true = whitelist, false = blacklist")
    public static void setMineWhiteList(boolean isWhitelist) {
        ErosionBrewApi.setMineWhiteList(isWhitelist);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the current list of destroyable blocks is a Whitelist")
    public static boolean isDestroyWhitelist() {
        return ErosionBrewApi.isDestroyWhitelist();
    }

    @ZenMethod
    @ZenDoc(value="Used to invert the condition of the destroyable blocks list. true = whitelist, false = blacklist")
    public static void setDestroyWhitelist(boolean isWhitelist) {
        ErosionBrewApi.setDestroyWhitelist(isWhitelist);
    }

    @ZenMethod
    @ZenDoc(value="Sets a maximum harvest level that the brew can mine/destroy. Returns the previous max harvest level value")
    public static int setMaxHarvestLevel(int newLevel) {
        return ErosionBrewApi.setMaxHarvestLevel(newLevel);
    }

    @ZenMethod
    @ZenDoc(value="Returns the maximum harvest level that the brew can mine/destroy")
    public static int getMaximumHarvestLevel() {
        return ErosionBrewApi.getMaximumHarvestLevel();
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the target blockstate can either be destroyed or mined")
    public static boolean canAffect(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.canAffect(state);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate can be mined by the brew")
    public static boolean canMine(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.canMine(state);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate can be destroyed by the brew")
    public static boolean canDestroy(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.canDestroy(state);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new blockstate into the mineable blockstate list. Returns false if blockstate was already there")
    public static boolean registerMineable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.registerMineable(state);
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new blockstate from the mineable blocks list. Returns false if blockstate was not there")
    public static boolean unregisterMineable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.unregisterMineable(state);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new blockstate into the mineable blockstate list. Returns false if blockstate was already there")
    public static boolean registerDestroyable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.registerDestroyable(state);
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new blockstate from the mineable blocks list. Returns false if blockstate was not there")
    public static boolean unregisterDestroyable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewApi.unregisterDestroyable(state);
    }

}
