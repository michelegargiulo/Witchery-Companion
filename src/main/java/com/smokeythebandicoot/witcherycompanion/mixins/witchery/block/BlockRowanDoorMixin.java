package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.rowandoor.IBlockRowanDoorAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockRowanDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 Mixins:
 [Bugfix] Add compat for Rowan Doors with Triggered Dispersal
 [Integration] Expose the key check of Rowan Doors, used to stop Quark from opening locked Rowan Doors
 */
@Mixin(BlockRowanDoor.class)
public abstract class BlockRowanDoorMixin extends WitcheryBlockDoor implements IBlockRowanDoorAccessor {

    private BlockRowanDoorMixin(Material material) {
        super(material);
    }

    /** Private in W:R. Only reads the Player inventory and the key NBT (and adds an empty tag to a key without one) */
    @Shadow(remap = false)
    protected abstract boolean hasKeyForDoor(World world, BlockPos pos, IBlockState state, EntityPlayer player);

    @Override
    public boolean witcherycompanion$accessor$hasKeyForDoor(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return this.hasKeyForDoor(world, pos, state, player);
    }

    /** This Mixin allows the Rowan Door block to contain a TileEntity */
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
