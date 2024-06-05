package com.smokeythebandicoot.witcherycompanion.integrations.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.Collections;
import java.util.HashSet;

public class CauldronApi {

    // Blockstate heat sources
    private static final HashSet<IBlockState> heatSourceBlockStates = new HashSet<>();

    // Block heat sources. In default Witchery, FIRE is the only heat source
    private static HashSet<Block> heatSourceBlocks = new HashSet<>(
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

    /** Internal usage */
    public static Block getFireBlock() {
        return Blocks.FIRE;
    }

}
