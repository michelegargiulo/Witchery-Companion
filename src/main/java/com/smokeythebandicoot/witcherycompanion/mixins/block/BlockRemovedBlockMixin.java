package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockRemovedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 Mixins:
 [Bugfix] Brew of Tidal Hold suffocates entities that pass inside of the hole
 */
@Mixin(value = BlockRemovedBlock.class)
public abstract class BlockRemovedBlockMixin extends BlockContainer {

    @Shadow(remap = true)
    public abstract TileEntity createNewTileEntity(World worldIn, int meta);

    private BlockRemovedBlockMixin(Material materialIn) {
        super(materialIn);
    }

    /** Injects the isPassable() function into the target class */
    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    /** Injects the isFullCube() function into the target class */
    @Override
    public boolean isFullCube(IBlockState state) {
        // Problem is here: if it is a full cube, the entity suffocates, depending on the material
        // By setting this to false, the entity does not suffocate regardless of the material of the block
        return !ModConfig.PatchesConfiguration.BrewsTweaks.tidalHold_fixEntitySuffocation;
    }
}
