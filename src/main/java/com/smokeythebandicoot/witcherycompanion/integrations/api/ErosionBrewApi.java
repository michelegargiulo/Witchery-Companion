package com.smokeythebandicoot.witcherycompanion.integrations.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.init.WitcheryBlocks;

import java.util.Arrays;
import java.util.HashSet;

public class ErosionBrewApi {

    // Mine List: blocks that are here will be mined by the brew
    public static HashSet<net.minecraft.block.state.IBlockState> mineList = new HashSet<>(
            Arrays.asList(Blocks.OBSIDIAN.getDefaultState()));

    // Destroy List: blocks that are here and not in mineList will be set to air by the brew
    public static HashSet<IBlockState> destroyList = new HashSet<>(
            Arrays.asList(Blocks.BEDROCK.getDefaultState(), WitcheryBlocks.BARRIER.getDefaultState()));

    // Maximum harvest level that the brew can destroy/mine
    public static int maximumHarvestLevel = -1;

    // Whitelist / Blacklist both lists separately
    public static boolean mineWhiteList = true;
    public static boolean destroyWhiteList = false;

    public static boolean isMineWhiteList() {
        return mineWhiteList;
    }

    public static void setMineWhiteList(boolean isWhitelist) {
        mineWhiteList = isWhitelist;
    }

    public static boolean isDestroyWhitelist() {
        return destroyWhiteList;
    }

    public static void setDestroyWhitelist(boolean isWhitelist) {
        ErosionBrewApi.destroyWhiteList = isWhitelist;
    }

    public static int setMaxHarvestLevel(int newLevel) {
        int oldLevel = maximumHarvestLevel;
        maximumHarvestLevel = newLevel;
        return oldLevel;
    }

    public static int getMaximumHarvestLevel() {
        return maximumHarvestLevel;
    }

    public static boolean canAffect(IBlockState state) {
        return canMine(state) || canDestroy(state);
    }

    public static boolean canMine(IBlockState state) {
        return mineWhiteList == mineList.contains(state) &&      // Contained in mineList
                (maximumHarvestLevel == -1 || state.getBlock().getHarvestLevel(state) <= maximumHarvestLevel);  // And has low harvest level
    }

    public static boolean canDestroy(IBlockState state) {
        return  !(mineWhiteList == mineList.contains(state)) &&                                 // Not contained in mineList
                (destroyWhiteList == destroyList.contains(state)) &&                            // But contained in destroyList
                (maximumHarvestLevel == -1 || state.getBlock().getHarvestLevel(state) <= maximumHarvestLevel);   // Has low harvest level
    }

    public static boolean registerMineable(IBlockState state) {
        boolean alreadyRegistered = mineList.contains(state);
        mineList.add(state);
        return !alreadyRegistered;
    }

    public static boolean unregisterMineable(IBlockState state) {
        boolean wasRegistered = mineList.contains(state);
        mineList.remove(state);
        return wasRegistered;
    }

    public static boolean registerDestroyable(IBlockState state) {
        boolean alreadyRegistered = destroyList.contains(state);
        destroyList.add(state);
        return !alreadyRegistered;
    }

    public static boolean unregisterDestroyable(IBlockState state) {
        boolean wasRegistered = destroyList.contains(state);
        destroyList.remove(state);
        return wasRegistered;
    }
}
