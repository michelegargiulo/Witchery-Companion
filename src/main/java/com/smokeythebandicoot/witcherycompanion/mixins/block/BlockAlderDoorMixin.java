package com.smokeythebandicoot.witcherycompanion.mixins.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.block.BlockAlderDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(BlockAlderDoor.class)
public abstract class BlockAlderDoorMixin extends WitcheryBlockDoor {

    private BlockAlderDoorMixin(Material material) {
        super(material);
    }

    @Inject(method = "hasTileEntity", remap = false, cancellable = true, at = @At("HEAD"))
    private void alwaysHasTileEntity(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
