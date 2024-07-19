package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class BarkBeltApi {

    public static HashSet<IBlockState> validStates;
    public static HashSet<Block> validBlocks;

    {
        // By default, no blockstates are defined by Witchery
        validStates = new HashSet<>();

        // By default, GRASS and MYCELIUM are checked by Witchery
        validBlocks = new HashSet<>();
        validBlocks.add(Blocks.GRASS);
        validBlocks.add(Blocks.MYCELIUM);
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

    public static List<List<IBlockState>> getRechargers() {
        List<List<IBlockState>> finalStates = new ArrayList<>();
        for (Block block : validBlocks) {
            List<IBlockState> blockStates = new ArrayList<>(block.getBlockState().getValidStates());
            finalStates.add(blockStates);
        }
        for (IBlockState state : validStates) {
            finalStates.add(Collections.singletonList(state));
        }
        return finalStates;
    }

}
