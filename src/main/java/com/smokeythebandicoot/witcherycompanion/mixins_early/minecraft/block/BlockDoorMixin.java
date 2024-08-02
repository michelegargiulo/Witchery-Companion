package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

/**
 Mixins:
 [Feature] Implement ICursableTrigger to attach Curse Trigger behaviour on Door activation (by player).
    For entities is done in EntityAIOpenDoorMixin
 */
@Mixin(BlockDoor.class)
public class BlockDoorMixin extends Block implements ICursableTrigger {

    private BlockDoorMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin calls the trigger when the Player right-clicks the door. The injection point is AFTER the
     * check for whether the door is made of Iron */
    @Inject(method = "onBlockActivated", remap = true, at = @At(value = "RETURN", ordinal = 2))
    private void triggerEffect(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        this.onTrigger(worldIn, pos, playerIn);
    }

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

    /** This Mixin is responsible for destroying the TE when the block is broken */
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(this.getEffectivePos(worldIn, pos));
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public void spawnParticles(World world, BlockPos impactPos, BlockPos effectivePos) {
        if (effectivePos == null || !isTriggerEnabled() || (!(world instanceof WorldServer)))
            return;

        WorldServer worldServer = (WorldServer)world;

        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                effectivePos.getX() + 0.5, effectivePos.getY() + 0.5,  effectivePos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);

        BlockPos otherDoorPiecePos = effectivePos.up().toImmutable();
        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                otherDoorPiecePos.getX() + 0.5, otherDoorPiecePos.getY() + 0.5,  otherDoorPiecePos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);
    }

    @Override
    public boolean isTriggerEnabled() {
        return TriggeredDispersalTweaks.enable_dispersalRework &&
                TriggeredDispersalTweaks.enable_door;
    }
}
