package com.smokeythebandicoot.witcherycompanion.api.ritemovingearth;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.init.WitcheryBlocks;

import java.util.HashSet;

public class RiteOfMovingEarthApi {

    private static final HashSet<IBlockState> blacklistedStates;
    private static final HashSet<Block> blacklistedBlocks;

    static {
        blacklistedStates = new HashSet<>();
        blacklistedBlocks = new HashSet<>();

        // Vanilla blocks
        blacklistedBlocks.add(Blocks.BEDROCK);

        // Witchery blocks
        // -- Enabled by default in Witchery
        blacklistedBlocks.add(WitcheryBlocks.ALTAR);
        blacklistedBlocks.add(WitcheryBlocks.VOID_BRAMBLE);
        // -- Added by Companion
        blacklistedBlocks.add(WitcheryBlocks.MIRROR_WALL);
        blacklistedBlocks.add(WitcheryBlocks.UNBREAKABLE_MIRROR);
    }

    public static void blacklistBlock(Block block) {
        blacklistedBlocks.add(block);
    }

    public static void blacklistBlockState(IBlockState state) {
        blacklistedStates.add(state);
    }

    public static void allowBlock(Block block) {
        blacklistedBlocks.remove(block);
    }

    public static void allowBlockState(IBlockState state) {
        blacklistedStates.remove(state);
    }

    public static boolean canBeMoved(IBlockState state) {
        return !(blacklistedBlocks.contains(state.getBlock()) || blacklistedStates.contains(state));
    }

}
