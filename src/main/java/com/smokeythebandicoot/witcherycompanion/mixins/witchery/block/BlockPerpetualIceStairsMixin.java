package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.block.BlockPerpetualIceStairs;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Mixins:
 * [Bugfix] Fix Ice Stairs sides not rendering when placed aside to other non-full ice material blocks
 * [Bugfix] Fix Ice Stairs x-raying through other blocks with their full sides
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin(BlockPerpetualIceStairs.class)
public abstract class BlockPerpetualIceStairsMixin extends BlockStairs {

    private BlockPerpetualIceStairsMixin(IBlockState modelState) {
        super(modelState);
        this.setDefaultSlipperiness(0.98F);
        this.setLightOpacity(3);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(WitcheryGeneralItems.TAB);
    }

    /** Since render layer is translucent, all sides do not block rendering in any case (x-ray bug) **/
    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    /** This Mixin fixes the logic that W:R decides which sides of the block to render with. The bug is that W:R only checks for
     * the opposing block's material. If it's ice, the face does not render, even if the other side does not block rendering.
     * Special care is used for some other Perpetual Ice blocks, to make it seem seamless when placed one aside the other **/
    @Inject(method = "shouldSideBeRendered", remap = true, cancellable = true, at = @At("HEAD"))
    private void fixNonRenderedSides(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        // Opposing block is solid and stops rendering: avoid rendering the face
        IBlockState opposingBlock = blockAccess.getBlockState(pos.offset(side));
        if (opposingBlock.doesSideBlockRendering(blockAccess, pos, side.getOpposite())) {
            cir.setReturnValue(false);
        }

        // If connected to perpetual ice blocks that have full faces or matching faces, return false
        cir.setReturnValue(
                opposingBlock.getBlock() != Blocks.ICE &&
                opposingBlock.getBlock() != WitcheryBlocks.PERPETUAL_ICE &&
                opposingBlock.getBlock() != WitcheryBlocks.PERPETUAL_ICE_SLAB_DOUBLE &&
                opposingBlock.getBlock() != WitcheryBlocks.PERPETUAL_ICE_STAIRS
        );
    }

}
