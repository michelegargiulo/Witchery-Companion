package com.smokeythebandicoot.witcherypatcher.mixins.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockRemovedBlock;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

/**
 Mixins:
 [Bugfix] Brew of Tidal Hold suffocates entities that pass inside of the hole
 */
@Mixin(value = BlockRemovedBlock.class, remap = false)
public class BlockRemovedBlockMixin extends BlockContainer {

    private BlockRemovedBlockMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return WitcheryTileEntities.REMOVED.create();
    }
}
