package com.smokeythebandicoot.witcherycompanion.mixins.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.block.BlockRowanDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;

/**
 Mixins:
 [Bugfix] Add compat for Rowan Doors with Triggered Dispersal
 */
@Mixin(BlockRowanDoor.class)
public abstract class BlockRowanDoorMixin extends WitcheryBlockDoor {

    private BlockRowanDoorMixin(Material material) {
        super(material);
    }

    /** This Mixin allows the Rowan Door block to contain a TileEntity */
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
