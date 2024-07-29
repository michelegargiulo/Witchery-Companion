package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

/**
 Mixins:
 [Feature] Implement ICursableTrigger to attach Curse Trigger behaviour on Pressure Plate activation
 */
@Mixin(BlockBasePressurePlate.class)
public abstract class BlockBasePressurePlateMixin extends Block implements ICursableTrigger {

    private BlockBasePressurePlateMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin calls the trigger when the Entity steps on the Pressure Plate */
    @Inject(method = "onEntityCollision", remap = true, at = @At(value = "INVOKE", remap = true, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/block/BlockBasePressurePlate;updateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)V"))
    private void triggerEffect(World worldIn, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci) {
        this.onTrigger(worldIn, pos, entity);
    }

    /** This Mixin is responsible for destroying the TE when the block is broken */
    @Inject(method = "breakBlock", remap = true, at = @At("TAIL"))
    public void removeTileEntityOnBreak(World worldIn, BlockPos pos, IBlockState state, CallbackInfo ci) {
        Utils.logChat("Block broken, destroy TE");
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

}
