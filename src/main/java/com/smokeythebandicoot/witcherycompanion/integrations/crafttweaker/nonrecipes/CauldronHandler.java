package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collections;
import java.util.HashSet;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Cauldron")
@ZenRegister
public class CauldronHandler {

    private static HashSet<IBlockState> heatSourceBlockStates = new HashSet<>();

    // In default Witchery, FIRE is the only heat source
    private static HashSet<Block> heatSourceBlocks = new HashSet<>(
            Collections.singletonList(Blocks.FIRE));

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as an IBlockState. Must be placed immediately below the cauldron")
    public static void registerHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        IBlockState state = CraftTweakerMC.getBlockState(blockstate);
        heatSourceBlockStates.add(state);
    }

    @ZenMethod
    @ZenDoc(value="Registers an heat source specified as a Block. Must be placed immediately below the cauldron")
    public static void registerHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        heatSourceBlocks.add(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlockstate(crafttweaker.api.block.IBlockState blockstate) {
        IBlockState state = CraftTweakerMC.getBlockState(blockstate);
        heatSourceBlockStates.remove(state);
    }

    @ZenMethod
    @ZenDoc(value="Removes an heat source specified as a Block")
    public static void removeHeatSourceBlock(crafttweaker.api.block.IBlock block) {
        heatSourceBlocks.remove(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the specified IBlockState is a valid Cauldron Heat Source")
    public static boolean isHeatSource(IBlockState state) {
        return heatSourceBlockStates.contains(state) || heatSourceBlocks.contains(state.getBlock());
    }

}
