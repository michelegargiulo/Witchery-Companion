package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockAltar;

public interface IBlockAltarAccessor {

    BlockPos witcherycompanion$accessor$getCore(IBlockAccess world, BlockPos pos);

    /** The blocks that form the Altar the block at pos belongs to, or null if it is not part of a complete Altar */
    BlockAltar.AltarPatternMatch witcherycompanion$accessor$findParts(IBlockAccess world, BlockPos pos);

}
