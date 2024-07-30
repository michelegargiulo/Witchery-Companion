package com.smokeythebandicoot.witcherycompanion.mixins.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.block.BlockRowanDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRowanDoor.class)
public abstract class BlockRowanDoorMixin extends WitcheryBlockDoor {

    private BlockRowanDoorMixin(Material material) {
        super(material);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
