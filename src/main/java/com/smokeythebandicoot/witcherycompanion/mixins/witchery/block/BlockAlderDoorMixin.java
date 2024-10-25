package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAlderDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 [Bugfix] Fix Redstone Behavior
 */
@ParametersAreNonnullByDefault
@Mixin(BlockAlderDoor.class)
public abstract class BlockAlderDoorMixin extends WitcheryBlockDoor {

    private BlockAlderDoorMixin(Material material) {
        super(material);
    }

    /** This Mixin avoids not having a TileEntity when the door is not disguised **/
    @Inject(method = "hasTileEntity", remap = false, cancellable = true, at = @At("HEAD"))
    private void alwaysHasTileEntity(IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    /** This Mixin reacts to neighbors changed as if the door was always open. This makes it so that the door
     * does not react to redstone **/
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.alderDoor_fixRedstoneBehavior) {
            super.neighborChanged(state.withProperty(OPEN, true), worldIn, pos, blockIn, fromPos);
            return;
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    /** This Mixin is an additional check to avoid reacting to weak redstone power **/
    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.alderDoor_fixRedstoneBehavior) {
            return false;
        }
        return super.shouldCheckWeakPower(state, world, pos, side);
    }
}
