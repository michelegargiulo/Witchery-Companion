package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import kotlin.jvm.functions.Function1;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockPerpetualIceSlab;
import net.msrandom.witchery.block.WitcheryBlockSlab;
import net.msrandom.witchery.init.WitcheryBlocks;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mixin(BlockPerpetualIceSlab.class)
public abstract class BlockPerpetualIceSlabMixin extends WitcheryBlockSlab {

    private BlockPerpetualIceSlabMixin(Function1<? super BlockSlab, ? extends BlockSlab> singleSlab) {
        super(Material.ICE, singleSlab);
        this.setLightOpacity(3);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
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
        IBlockState opposingBlock = blockAccess.getBlockState(pos.offset(side));
        if (opposingBlock.doesSideBlockRendering(blockAccess, pos, side.getOpposite())) {
            return false;
        }

        // Perpetual ice slabs with the same face
        if (opposingBlock.getBlock() == WitcheryBlocks.PERPETUAL_ICE_SLAB) {
            if (opposingBlock.getValue(HALF) == blockState.getValue(HALF)) {
                return false;
            }
        }

        // Perpetual ice blocks that cover the same faces
        if (opposingBlock.getBlock() instanceof BlockStairs &&
            ( // Stairs and Slab top half or Stairs and Slab bottom half. Since opposingBlock is BlockStairs and this is BlockSlab, there shouldn't be any invalid properties
                (opposingBlock.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP && blockState.getValue(HALF) == EnumBlockHalf.TOP) ||
                (opposingBlock.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.BOTTOM && blockState.getValue(HALF) == EnumBlockHalf.BOTTOM)
            ))
        {
            return false;
        }

        // Full Perpetual ice blocks
        return opposingBlock.getBlock() != WitcheryBlocks.PERPETUAL_ICE &&
                opposingBlock.getBlock() != WitcheryBlocks.PERPETUAL_ICE_SLAB_DOUBLE;

    }


}
