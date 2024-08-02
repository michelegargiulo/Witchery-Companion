package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import crafttweaker.api.block.IBlock;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixin:
 [Feature] Compat with Triggered Dispersal brews
 */
@Mixin(BlockBed.class)
public abstract class BlockBedMixin extends BlockHorizontal implements ICursableTrigger {

    @Shadow(remap = true) @Final
    public static PropertyEnum<BlockBed.EnumPartType> PART;

    private BlockBedMixin(Material materialIn) {
        super(materialIn);
    }

    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = true,
          target = "Lnet/minecraft/entity/player/EntityPlayer;trySleep(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/player/EntityPlayer$SleepResult;"))
    private EntityPlayer.SleepResult triggerOnPlayerSleep(EntityPlayer instance, BlockPos bedLocation, Operation<EntityPlayer.SleepResult> original) {
        EntityPlayer.SleepResult result = original.call(instance, bedLocation);
        if (result == EntityPlayer.SleepResult.OK) {
            this.onTrigger(instance.world, bedLocation, instance);
        }
        return result;
    }

    /** This Override is required because for some reason vanilla uses two TileEntities
     * for the bed. Only one will be used for Triggered Dispersal, the HEAD part */
    @Override
    public BlockPos getEffectivePos(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
            pos = pos.offset(state.getValue(FACING));
            return world.getBlockState(pos).getBlock() == Blocks.BED ? pos : null;
        }
        return pos;
    }

    @Override
    public void spawnParticles(World world, BlockPos impactPos, BlockPos effectivePos) {
        if (effectivePos == null || !isTriggerEnabled() || (!(world instanceof WorldServer)))
            return;
        WorldServer worldServer = (WorldServer)world;
        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                effectivePos.getX() + 0.5, effectivePos.getY() + 0.5,  effectivePos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);

        // effectivePos is always at bed head: get the other part of the bed
        IBlockState state = world.getBlockState(effectivePos);
        EnumFacing bedFacing = state.getValue(FACING).getOpposite();
        BlockPos otherPos = effectivePos.offset(bedFacing).toImmutable();

        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                otherPos.getX() + 0.5, otherPos.getY() + 0.5,  otherPos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);
    }

    @Override
    public boolean isTriggerEnabled() {
        return TriggeredDispersalTweaks.enable_dispersalRework &&
                TriggeredDispersalTweaks.enable_beds;
    }
}
