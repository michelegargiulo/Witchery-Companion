package com.smokeythebandicoot.witcherypatcher.integrations.crafttweaker.nonrecipes;

import com.smokeythebandicoot.witcherypatcher.integrations.crafttweaker.bridge.ErosionBrewBridge;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherypatcher.BrewOfErosion")
@ZenRegister
public class ErosionBrewHandler {

    @ZenMethod
    @ZenDoc(value="Returns true if the current list of mineable blocks is a Whitelist")
    public static boolean isMineWhiteList() {
        return ErosionBrewBridge.isMineWhiteList();
    }

    @ZenMethod
    @ZenDoc(value="Used to invert the condition of the mineable blocks list. true = whitelist, false = blacklist")
    public static void setMineWhiteList(boolean isWhitelist) {
        ErosionBrewBridge.setMineWhiteList(isWhitelist);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the current list of destroyable blocks is a Whitelist")
    public static boolean isDestroyWhitelist() {
        return ErosionBrewBridge.isDestroyWhitelist();
    }

    @ZenMethod
    @ZenDoc(value="Used to invert the condition of the destroyable blocks list. true = whitelist, false = blacklist")
    public static void setDestroyWhitelist(boolean isWhitelist) {
        ErosionBrewBridge.setDestroyWhitelist(isWhitelist);
    }

    @ZenMethod
    @ZenDoc(value="Sets a maximum harvest level that the brew can mine/destroy")
    public static int setMaxHarvestLevel(int newLevel) {
        return ErosionBrewBridge.setMaxHarvestLevel(newLevel);
    }

    @ZenMethod
    @ZenDoc(value="Returns the maximum harvest level that the brew can mine/destroy")
    public static int getMaximumHarvestLevel() {
        return ErosionBrewBridge.getMaximumHarvestLevel();
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the target blockstate can either be destroyed or mined")
    public static boolean canAffect(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.canAffect(state);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate can be destroyed by the brew")
    public static boolean canMine(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.canMine(state);
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate can be destroyed by the brew")
    public static boolean canDestroy(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.canDestroy(state);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new blockstate into the mineable blockstate list. Returns false if blockstate was already there")
    public static boolean registerMineable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.registerMineable(state);
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new blockstate from the mineable blocks list. Returns false if blockstate was not there")
    public static boolean unregisterMineable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.unregisterMineable(state);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new blockstate into the mineable blockstate list. Returns false if blockstate was already there")
    public static boolean registerDestroyable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.registerDestroyable(state);
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new blockstate from the mineable blocks list. Returns false if blockstate was not there")
    public static boolean unregisterDestroyable(IBlockState iBlockState) {
        net.minecraft.block.state.IBlockState state = CraftTweakerMC.getBlockState(iBlockState);
        return ErosionBrewBridge.unregisterDestroyable(state);
    }

}
