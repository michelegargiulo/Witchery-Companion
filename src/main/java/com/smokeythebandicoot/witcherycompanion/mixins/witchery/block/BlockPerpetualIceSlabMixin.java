package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import kotlin.jvm.functions.Function1;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockPerpetualIceSlab;
import net.msrandom.witchery.block.WitcheryBlockSlab;
import net.msrandom.witchery.init.WitcheryBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Mixins:
 * [Bugfix] Fix Ice slabs not being slippery
 * [Tweak] Improve side rendering with translucency
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mixin(BlockPerpetualIceSlab.class)
public abstract class BlockPerpetualIceSlabMixin extends WitcheryBlockSlab {

    private BlockPerpetualIceSlabMixin(Function1<? super BlockSlab, ? extends BlockSlab> singleSlab) {
        super(Material.ICE, singleSlab);
    }

    @Inject(method = "<init>", remap = false, at = @At("TAIL"))
    private void fixMissingSlipperiness(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.perpetualIceSlabs_fixSlipperiness) {
            this.setDefaultSlipperiness(0.98F);
        }
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /** Since render layer is translucent, all sides do not block rendering in any case (x-ray bug) **/
    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    /**
     * This Mixin fixes the logic that W:R decides which sides of the block to render with. The bug is that W:R only checks for
     * the opposing block's material. If it's ice, the face does not render, even if the other side does not block rendering.
     * Special care is used for some other Perpetual Ice blocks, to make it seem seamless when placed one aside the other
     **/
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {

        // Opposing block is solid and stops rendering: avoid rendering the face
        IBlockState opposingState = blockAccess.getBlockState(pos.offset(side));
        if (opposingState.doesSideBlockRendering(blockAccess, pos, side.getOpposite())) {
            return false;
        }

        Block opposingBlock = opposingState.getBlock();

        if (opposingBlock == WitcheryBlocks.PERPETUAL_ICE || opposingBlock == WitcheryBlocks.PERPETUAL_ICE_SLAB_DOUBLE || opposingBlock == Blocks.ICE) {
            return cullToFull(blockState, opposingState, side);
        }

        // Perpetual ice slabs with the same face or is double
        else if (opposingBlock == WitcheryBlocks.PERPETUAL_ICE_SLAB) {
            return cullToSlab(blockState, opposingState, side);
        }

        // Opposing block are stairs
        else if (opposingBlock instanceof BlockStairs) {
            return cullToStairs(blockState, opposingState, side);
        }

        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);

    }

    @Unique
    private boolean cullToFull(IBlockState state, IBlockState otherState, EnumFacing side) {

        // Two full touching, skip
        if (isDouble())
            return false;

        // Full and half: if the blocks share a face, skip rendering
        else {
            // If up or down, only correct half will skip rendering
            if (side == EnumFacing.UP) {
                return state.getValue(HALF) != EnumBlockHalf.TOP;
            }
            else if (side == EnumFacing.DOWN) {
                return state.getValue(HALF) != EnumBlockHalf.BOTTOM;
            }
        }

        // Adjacent horizontally, we always skip
        return false;
    }

    @Unique
    private boolean cullToSlab(IBlockState state, IBlockState otherState, EnumFacing side) {

        // Wrong call, other is not slab
        if (!(otherState.getBlock() instanceof BlockSlab)) {
            return true;
        }

        BlockSlab otherBlock = (BlockSlab) otherState.getBlock();

        // We are double: only skip if full face
        if (isDouble()) {

            if (otherBlock.isDouble()) {
                return false;
            }

            // Two full touching -> skip
            // Other is not full -> if UP or DOWN, skip if they share a face
            if (side == EnumFacing.UP) {
                return otherState.getValue(HALF) != EnumBlockHalf.BOTTOM;
            }
            else if (side == EnumFacing.DOWN) {
                return otherState.getValue(HALF) != EnumBlockHalf.TOP;
            }
            // If other not full and touch horizontally, we always render, other skips
            return true;
        }

        // We are single: if other is full or slab with same half, skip. Otherwise, render
        else {

            BlockSlab.EnumBlockHalf half = state.getValue(HALF);

            if (otherBlock == WitcheryBlocks.PERPETUAL_ICE_SLAB_DOUBLE) {
                // Side is UP, only correct half skips
                if (side == EnumFacing.UP) {
                    return half == EnumBlockHalf.TOP;
                }
                // Side is UP, only correct half skips
                else if (side == EnumFacing.DOWN) {
                    return half == EnumBlockHalf.BOTTOM;
                }
                // Side is any horizontal, we always skip
                else {
                    return false;
                }
            }
            else {
                // Two half slabs touching: they cull to opposite halves for UP-DOWN sides
                if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
                    return half == otherState.getValue(HALF);
                }
                // They cull to same halves for any horizontal side
                else {
                    return half != otherState.getValue(HALF);
                }
            }
        }
    }

    @Unique
    private boolean cullToStairs(IBlockState state, IBlockState otherState, EnumFacing side) {

        // Wrong call, other is not slab
        if (!(otherState.getBlock() instanceof BlockStairs)) {
            return true;
        }

        BlockStairs otherBlock = (BlockStairs) otherState.getBlock();

        // We are double, so we look for full faces to cull
        if (isDouble()) {
            // If up, then stairs must be placed facing up
            if (side == EnumFacing.UP) {
                return otherState.getValue(BlockStairs.HALF) != BlockStairs.EnumHalf.BOTTOM;
            }
            // If up, then stairs must be placed upside-down
            else if (side == EnumFacing.DOWN) {
                return otherState.getValue(BlockStairs.HALF) != BlockStairs.EnumHalf.TOP;
            }
            // If horizontal side, stairs must be placed against us
            else {
                return otherState.getValue(BlockStairs.FACING) != side.getOpposite();
            }
        }

        else {
            EnumBlockHalf half = state.getValue(HALF);
            BlockStairs.EnumHalf otherHalf = otherState.getValue(BlockStairs.HALF);
            // up side, skip if we are top half and stairs are bottom
            if (side == EnumFacing.UP) {
                return half != EnumBlockHalf.TOP || otherHalf != BlockStairs.EnumHalf.BOTTOM;
            }
            // Inverted for down side
            else if (side == EnumFacing.DOWN) {
                return half != EnumBlockHalf.BOTTOM || otherHalf != BlockStairs.EnumHalf.TOP;
            }
            // For horizontal sides, we only skip if we are same half as stairs, or other half but same facing as side
            else {
                if ((half == EnumBlockHalf.BOTTOM && otherHalf == BlockStairs.EnumHalf.BOTTOM) ||
                    (half == EnumBlockHalf.TOP && otherHalf == BlockStairs.EnumHalf.TOP)) {
                    return false;
                }
                // We are opposite sides
                else {
                    return otherState.getValue(BlockStairs.FACING) != side.getOpposite();
                }
            }

        }

    }
}
