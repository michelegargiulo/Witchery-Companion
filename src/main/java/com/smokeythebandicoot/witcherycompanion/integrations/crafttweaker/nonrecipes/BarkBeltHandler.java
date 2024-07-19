package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes;


import com.smokeythebandicoot.witcherycompanion.api.BarkBeltApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.BrewOfErosion")
@ZenRegister
public class BarkBeltHandler {

    @ZenMethod
    @ZenDoc(value="Registers a new Block as a valid block that recharged Bark Belt")
    public static boolean registerBlock(IBlock block) {
        return BarkBeltApi.registerBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Registers a new Blockstate as a valid block that recharged Bark Belt")
    public static boolean registerBlockstate(IBlockState state) {
        return BarkBeltApi.registerBlockstate(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new Block as a valid block that recharged Bark Belt")
    public static boolean removeBlock(IBlock block) {
        return BarkBeltApi.removeBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a new Blockstate as a valid block that recharged Bark Belt")
    public static boolean removeBlockstate(IBlockState state) {
        return BarkBeltApi.removeBlockstate(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the block can recharge Bark Belt")
    public static boolean canRechargeBarkBelt(IBlock block) {
        return BarkBeltApi.canRechargeBarkBelt(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate can recharge Bark Belt")
    public static boolean canRechargeBarkBelt(IBlockState state) {
        return BarkBeltApi.canRechargeBarkBelt(CraftTweakerMC.getBlockState(state));
    }
}
