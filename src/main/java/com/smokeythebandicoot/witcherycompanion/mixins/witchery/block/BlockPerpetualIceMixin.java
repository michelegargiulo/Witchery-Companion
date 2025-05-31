package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockPerpetualIce;
import net.msrandom.witchery.init.WitcheryBlocks;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mixin(BlockPerpetualIce.class)
public abstract class BlockPerpetualIceMixin extends BlockIce {

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState otherState = blockAccess.getBlockState(pos.offset(side));
        Block otherBlock = otherState.getBlock();

        // Cull against full
        if (otherBlock == WitcheryBlocks.PERPETUAL_ICE_SLAB_DOUBLE || otherBlock == Blocks.ICE) {
            return false;
        }

        if (otherBlock == WitcheryBlocks.PERPETUAL_ICE_SLAB) {
            // Cull against slabs
            BlockSlab.EnumBlockHalf slabHalf = otherState.getValue(BlockSlab.HALF);

            // If up or down, cull if slab has correct half
            if (side == EnumFacing.UP) {
                return slabHalf != BlockSlab.EnumBlockHalf.BOTTOM;
            }
            else if (side == EnumFacing.DOWN) {
                return slabHalf != BlockSlab.EnumBlockHalf.TOP;
            }
            // If side, we never cull, but slab does
            return true;
        }

        // Cull against stairs
        if (otherBlock == WitcheryBlocks.PERPETUAL_ICE_STAIRS) {

            BlockStairs.EnumHalf stairsHalf = otherState.getValue(BlockStairs.HALF);

            // If up or down, cull if slab has correct half
            if (side == EnumFacing.UP) {
                return stairsHalf != BlockStairs.EnumHalf.BOTTOM;
            }
            else if (side == EnumFacing.DOWN) {
                return stairsHalf != BlockStairs.EnumHalf.TOP;
            }
            // If side, we only cull if stairs are facing against us with their full side
            else {
                EnumFacing stairsFacing = otherState.getValue(BlockStairs.FACING);
                return stairsFacing != side.getOpposite();
            }

        }

        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

}
