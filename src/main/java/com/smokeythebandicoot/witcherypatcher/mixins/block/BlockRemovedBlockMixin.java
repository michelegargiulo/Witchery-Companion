package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
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
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

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

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        // Problem is here: if it is a full cube, the entity suffocates, depending on the material
        // By setting this to false, the entity does not suffocate regardless of the material of the block
        return !ModConfig.PatchesConfiguration.BrewsTweaks.tidalHold_fixEntitySuffocation;
    }
}
