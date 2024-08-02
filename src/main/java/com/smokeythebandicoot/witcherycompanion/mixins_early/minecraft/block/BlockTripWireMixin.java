package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import crafttweaker.api.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Feature] Add compat for Triggered Dispersal
 */
@Mixin(BlockTripWire.class)
public abstract class BlockTripWireMixin extends Block implements ICursableTrigger {

    @Shadow(remap = false) @Final
    public static PropertyBool ATTACHED;

    private BlockTripWireMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public BlockPos getEffectivePos(World world, BlockPos pos) {
        return witchery_Patcher$findHook(world, pos);
    }

    /** This Mixin is used to find the connected hook. The trigger won't be created on
     * disconnected wires/hooks, so a corresponding hook should be found by this function.
     * SOUTH / WEST is just a preference and won't matter. Trigger is lost upon disconnection */
    @Unique
    private BlockPos witchery_Patcher$findHook(World world, BlockPos pos) {

        for (EnumFacing enumfacing : new EnumFacing[] {EnumFacing.SOUTH, EnumFacing.WEST}) {
            for (int i = 1; i < 42; ++i) {
                BlockPos blockpos = pos.offset(enumfacing, i);
                IBlockState iblockstate = world.getBlockState(blockpos);

                // Found an hook
                if (iblockstate.getBlock() == Blocks.TRIPWIRE_HOOK) {
                    // If it faces the opposite searching direction, the hook is connected and the trigger is valid
                    if (iblockstate.getValue(BlockTripWireHook.FACING) == enumfacing.getOpposite()) {
                        // If the hook is not attached, it should not accept the potion.
                        // Setting effectivePos to null prevents particle spawning
                        if (iblockstate.getValue(ATTACHED)) {
                            return blockpos;
                        }
                        return null;
                    }
                    // Otherwise, no connection
                    break;
                }

                // We didn't find a hook and wire is terminated. No connection
                if (iblockstate.getBlock() != Blocks.TRIPWIRE) {
                    break;
                }
            }
        }

        return null;
    }

    @Inject(method = "onEntityCollision", remap = true, at = @At(value = "INVOKE", remap = true, shift = At.Shift.AFTER,
            target = "Lnet/minecraft/block/BlockTripWire;updateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void triggerOnEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci) {
        if (state.getValue(ATTACHED) &&
            TriggeredDispersalTweaks.enable_dispersalRework &&
            TriggeredDispersalTweaks.enable_tripwireHook) {
            this.onTrigger(world, pos, entity);
        }
    }

    @Override
    public void spawnParticles(World world, BlockPos impactPos, BlockPos effectivePos) {
        if (effectivePos == null || (!(world instanceof WorldServer)))
            return;
        WorldServer worldServer = (WorldServer)world;
        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                effectivePos.getX() + 0.5, effectivePos.getY() + 0.5,  effectivePos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);

        IBlockState thisHookState = world.getBlockState(effectivePos);
        if (thisHookState.getBlock() != Blocks.TRIPWIRE_HOOK) {
            return;
        }
        EnumFacing thisHookFacing = thisHookState.getValue(BlockTripWireHook.FACING);

        for (int i = 1; i < 43; i++) {
            BlockPos currentPos = effectivePos.offset(thisHookFacing, i);
            IBlockState currentState = world.getBlockState(currentPos);

            // If wire, spawn less particles
            if (currentState.getBlock() == Blocks.TRIPWIRE) {
                worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                        currentPos.getX() + 0.5, currentPos.getY() + 0.05,  currentPos.getZ() + 0.5,
                        5, 0.5, 0.1, 0.5, 0.5);
            }

            // If hook, spawn same amount of particles as first hook
            if (currentState.getBlock() == Blocks.TRIPWIRE_HOOK) {
                worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                        currentPos.getX() + 0.5, currentPos.getY() + 0.5,  currentPos.getZ() + 0.5,
                        25, 0.5, 0.5, 0.5, 0.5);
                return;
            }
        }


    }

}
