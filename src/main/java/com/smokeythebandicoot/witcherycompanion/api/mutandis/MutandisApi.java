package com.smokeythebandicoot.witcherycompanion.api.mutandis;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.HashSet;

public class MutandisApi {

    public static final HashMap<IBlockState, IBlockState> grassConversion;
    public static final HashMap<IBlockState, IBlockState> clayConversion;
    public static final HashSet<IBlockState> mutandis;
    public static final HashSet<IBlockState> mutandisExtremis;

    static {

        // PRIORITY 1 - this conversion is checked first
        // Clay Conversion: Blocks that get converted into others if water is on top. Conversion happens in patches (+ shape of blocks)
        clayConversion = new HashMap<>();
        clayConversion.put(Blocks.DIRT.getDefaultState(), Blocks.MYCELIUM.getDefaultState());

        // PRIORITY 2 - this conversion is checked second
        // Default conversion: Non-plant blocks that can be converted into other blocks
        grassConversion = new HashMap<>();
        grassConversion.put(Blocks.GRASS.getDefaultState(), Blocks.MYCELIUM.getDefaultState());
        grassConversion.put(Blocks.MYCELIUM.getDefaultState(), Blocks.GRASS.getDefaultState());

        // PRIORITY 2 - this conversion is checked second
        // Mutandis conversion: Plant blocks that can be converted into any other block of the set
        mutandis = new HashSet<>();
        // All vanilla saplings
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.JUNGLE));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK));

    }

}
