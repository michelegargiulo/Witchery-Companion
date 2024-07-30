package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.WitcheryBlockButton;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(WitcheryBlockDoor.class)
public abstract class WitcheryBlockDoorMixin extends BlockDoor implements ICursableTrigger {

    private WitcheryBlockDoorMixin(Material materialIn) {
        super(materialIn);
    }

    /*
    @Override
    public BlockPos getEffectivePos(World world, BlockPos pos) {
        // When potion hits lower half
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            return pos;
        }

        // For when the potion hits upper half
        IBlockState lowerState = world.getBlockState(pos.down());
        if (lowerState.getBlock() instanceof BlockDoor && lowerState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            return pos.down();
        }

        // for weird edge cases when setBlockstate is used to place half-doors
        return pos;
    }
     */

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
