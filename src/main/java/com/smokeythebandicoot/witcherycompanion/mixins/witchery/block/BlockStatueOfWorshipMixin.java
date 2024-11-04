package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockStatueOfWorship;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Tweak] Shrink AABB
 */
@ParametersAreNonnullByDefault
@Mixin(BlockStatueOfWorship.class)
public abstract class BlockStatueOfWorshipMixin extends BlockContainer {

    private BlockStatueOfWorshipMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin shrinks the AABB to be tighter around the statue **/
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.15, 0.75);
    }
}
