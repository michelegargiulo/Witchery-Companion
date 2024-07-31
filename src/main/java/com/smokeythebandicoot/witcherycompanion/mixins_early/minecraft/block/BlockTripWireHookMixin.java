package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 Mixins:
 [Feature] Add compat for Triggered Dispersal
 */
@Mixin(BlockTripWireHook.class)
public abstract class BlockTripWireHookMixin extends Block implements ICursableTrigger {

    @Shadow(remap = true) @Final
    public static PropertyBool ATTACHED;

    @Shadow(remap = true) @Final
    public static PropertyDirection FACING;

    private BlockTripWireHookMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin removes the trigger when a string is removed from the connected line,
     * breaking the mechanism. This is needed to have consisted behaviour between the two
     * triggers and to avoid player confusion */
    @Inject(method = "calculateState", remap = false, at = @At("TAIL"))
    private void removeTriggerOnDeattach(World worldIn, BlockPos pos, IBlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable IBlockState state, CallbackInfo ci) {
        if (!worldIn.getBlockState(pos).getValue(ATTACHED)) {
            worldIn.removeTileEntity(pos);
        }
    }

    /** This Mixin is required for keeping a TileEntityCursedTrigger inside the Hook block.
     * The return value makes it so that the trigger is lost upon disconnection */
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ATTACHED);
    }

    @Override
    public BlockPos getEffectivePos(World world, BlockPos pos) {
        if (world == null || pos == null) return null;

        // Retrieve state to check if attached and facing
        IBlockState state = world.getBlockState(pos);

        // If not attached, curses cannot be placed on this
        if (!state.getValue(ATTACHED)) return null;

        // TileEntity gets placed at the EAST or NORTH hook
        // If already the EAST or NORTH, then return pos
        EnumFacing facing = state.getValue(FACING);
        if (facing == EnumFacing.EAST || facing == EnumFacing.NORTH) return pos;

        // Otherwise, search for the other hook
        if (facing == EnumFacing.SOUTH) {
            return witchery_Patcher$findHook(world, pos, EnumFacing.SOUTH);
        } else if (facing == EnumFacing.WEST) {
            return witchery_Patcher$findHook(world, pos, EnumFacing.WEST);
        }
        // Called with impossible facing
        return null;
    }

    @Unique
    private BlockPos witchery_Patcher$findHook(World world, BlockPos pos, EnumFacing dir) {
        // It is the same as Vanilla + 1 because we must consider the opposite hook block, too
        for (int i = 1; i < 43; ++i) {
            BlockPos blockpos = pos.offset(dir, i);
            IBlockState iblockstate = world.getBlockState(blockpos);

            // Found an hook
            if (iblockstate.getBlock() == Blocks.TRIPWIRE_HOOK) {
                // If it faces the opposite searching direction, the hook is connected and the trigger is valid
                if (iblockstate.getValue(BlockTripWireHook.FACING) == dir.getOpposite()) {
                    return blockpos;
                }
                // Otherwise, no connection
                return null;
            }

            // We didn't find a hook and wire is terminated. No connection
            if (iblockstate.getBlock() != Blocks.TRIPWIRE) {
                return null;
            }
        }
        return null;
    }
}
