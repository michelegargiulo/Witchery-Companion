package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.init.WitcheryBlocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ErosionBrewApi {

    // Mine List: blocks that are here will be mined by the brew
    public static HashSet<IBlockState> mineList = initMiningList();

    // Destroy List: blocks that are here and not in mineList will be set to air by the brew
    public static HashSet<IBlockState> destroyList = initDestroyList();

    // Maximum harvest level that the brew can destroy/mine
    public static int maximumHarvestLevel = -1;

    // Whitelist / Blacklist both lists separately
    public static boolean mineWhiteList = true;
    public static boolean destroyWhiteList = false;

    /** Returns true if the mineList is a whitelist */
    public static boolean isMineWhiteList() {
        return mineWhiteList;
    }

    /** Sets the mineList as a whitelist or blacklist. true = whitelist, false = blacklist */
    public static void setMineWhiteList(boolean isWhitelist) {
        mineWhiteList = isWhitelist;
    }

    /** Returns true if the destroyList is a whitelist */
    public static boolean isDestroyWhitelist() {
        return destroyWhiteList;
    }

    /** Sets the destroyList as a whitelist or blacklist. true = whitelist, false = blacklist */
    public static void setDestroyWhitelist(boolean isWhitelist) {
        ErosionBrewApi.destroyWhiteList = isWhitelist;
    }

    /** Sets the Brew maximum harvest level. Blocks with higher harvest levels will be ignored */
    public static int setMaxHarvestLevel(int newLevel) {
        int oldLevel = maximumHarvestLevel;
        maximumHarvestLevel = newLevel;
        return oldLevel;
    }

    /** Returns the Brew maximum harvest level */
    public static int getMaximumHarvestLevel() {
        return maximumHarvestLevel;
    }

    /** Returns true if the blockstate can be mined or destroyed, false if it is ignored */
    public static boolean canAffect(IBlockState state) {
        return canMine(state) || canDestroy(state);
    }

    /** Returns true if the block can be mined by the brew */
    public static boolean canMine(IBlockState state) {
        return mineWhiteList == mineList.contains(state) &&      // Contained in mineList
                (maximumHarvestLevel == -1 || state.getBlock().getHarvestLevel(state) <= maximumHarvestLevel);  // And has low harvest level
    }

    /** Returns true if the block can be destroyed by the brew */
    public static boolean canDestroy(IBlockState state) {
        return  !(mineWhiteList == mineList.contains(state)) &&                                 // Not contained in mineList
                (destroyWhiteList == destroyList.contains(state)) &&                            // But contained in destroyList
                (maximumHarvestLevel == -1 || state.getBlock().getHarvestLevel(state) <= maximumHarvestLevel);   // Has low harvest level
    }

    /** Adds a new blockstate to the list of mineable blocks */
    public static boolean registerMineable(IBlockState state) {
        boolean alreadyRegistered = mineList.contains(state);
        mineList.add(state);
        return !alreadyRegistered;
    }

    /** Removes the blockstate from the list of mineable blocks */
    public static boolean unregisterMineable(IBlockState state) {
        boolean wasRegistered = mineList.contains(state);
        mineList.remove(state);
        return wasRegistered;
    }

    /** Adds a new blockstate to the list of destroyable blocks */
    public static boolean registerDestroyable(IBlockState state) {
        boolean alreadyRegistered = destroyList.contains(state);
        destroyList.add(state);
        return !alreadyRegistered;
    }

    /** Removes the blockstate from the list of destroyable blocks */
    public static boolean unregisterDestroyable(IBlockState state) {
        boolean wasRegistered = destroyList.contains(state);
        destroyList.remove(state);
        return wasRegistered;
    }

    /** This function is private and is used as an helper to init Mining List */
    private static HashSet<IBlockState> initMiningList() {
        return new HashSet<>(
                Collections.singletonList(Blocks.OBSIDIAN.getDefaultState()));
    }

    /** This function is private and is used as an helper to init Destroy List */
    private static HashSet<IBlockState> initDestroyList() {
        HashSet<IBlockState> set = new HashSet<>(
                Arrays.asList(
                        Blocks.BEDROCK.getDefaultState(),
                        WitcheryBlocks.BARRIER.getDefaultState()
                )
        );

        // If destroy list is a BLACKLIST, then add more unbreakable blocks to it
        if (ModConfig.PatchesConfiguration.BrewsTweaks.erosion_fixUnbreakables && !destroyWhiteList) {
            set.add(WitcheryBlocks.TORMENT_STONE.getDefaultState());
            set.add(WitcheryBlocks.TORMENT_PORTAL.getDefaultState());
            set.add(WitcheryBlocks.MIRROR.getDefaultState());
            set.add(WitcheryBlocks.MIRROR_WALL.getDefaultState());
            set.add(WitcheryBlocks.FORCE.getDefaultState());
            set.add(WitcheryBlocks.BARRIER.getDefaultState());
        }

        return set;
    }
}
