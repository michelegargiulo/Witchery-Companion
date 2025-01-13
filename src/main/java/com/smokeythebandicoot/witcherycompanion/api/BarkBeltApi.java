package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.*;

public class BarkBeltApi {

    public static HashSet<IBlockState> validStates = initStateList();
    public static HashSet<Block> validBlocks = initBlockList();

    // By default, Grass and Mycelium are valid blocks
    private static HashSet<Block> initBlockList() {
        HashSet<Block> blocks = new HashSet<>();
        blocks.add(Blocks.GRASS);
        blocks.add(Blocks.MYCELIUM);
        return blocks;
    }

    // By default, no blockstates are added by Witchery
    private static HashSet<IBlockState> initStateList() {
        return new HashSet<>();
    }

    /** Registers a new Block as a valid block that recharged Bark Belt */
    public static boolean registerBlock(Block block) {
        return validBlocks.add(block);
    }

    /** Registers a new Blockstate as a valid block that recharged Bark Belt */
    public static boolean registerBlockstate(IBlockState state) {
        return validStates.add(state);
    }

    /** Un-registers a new Block as a valid block that recharged Bark Belt */
    public static boolean removeBlock(Block block) {
        return validBlocks.remove(block);
    }

    /** Un-registers a new Blockstate as a valid block that recharged Bark Belt */
    public static boolean removeBlockstate(IBlockState state) {
        return validStates.remove(state);
    }

    /** Returns true if the block can recharge Bark Belt */
    public static boolean canRechargeBarkBelt(Block block) {
        return validBlocks.contains(block);
    }

    /** Returns true if the blockstate can recharge Bark Belt */
    public static boolean canRechargeBarkBelt(IBlockState state) {
        return validBlocks.contains(state.getBlock()) || validStates.contains(state);
    }

    public static Set<Set<IBlockState>> getRechargers() {
        Set<Set<IBlockState>> finalStates = new HashSet<>();
        for (Block block : validBlocks) {
            Set<IBlockState> blockStates = new HashSet<>(block.getBlockState().getValidStates());
            finalStates.add(blockStates);
        }
        for (IBlockState state : validStates) {
            // Avoid duplicate entries when player specifies both block and blockstate
            if (!validBlocks.contains(state.getBlock()))
                finalStates.add(Collections.singleton(state));
        }
        return finalStates;
    }

}
