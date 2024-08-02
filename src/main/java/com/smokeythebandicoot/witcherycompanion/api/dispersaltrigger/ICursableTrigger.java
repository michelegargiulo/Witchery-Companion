package com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger;

import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;

public interface ICursableTrigger {

    /** This method is responsible for containing recurring code for activating the trigger
     * Checks if the Block at 'pos' has a TileEntity and is a TileEntityCursedTrigger.
     * After the checks, applies the trigger. Eventually removes the TileEntity if
     * the trigger run out of curses. Returns true if the effect has been applied */
    default boolean onTrigger(World world, BlockPos pos, Entity entity) {

        if (!isTriggerEnabled()) return false;

        // Retrieve effectivePos for the specific implementation (Block-specific)
        BlockPos effectivePos = this.getEffectivePos(world, pos);

        // This should not return null here, but just in case
        if (effectivePos == null)
            return false;

        // Retrieve TE and perform checks
        TileEntity tile = world.getTileEntity(effectivePos);

        if (tile instanceof TileEntityCursedTrigger) {
            // Retrieve tile and apply effect
            TileEntityCursedTrigger trigger = (TileEntityCursedTrigger) tile;
            if (this.doApplyTrigger(entity, trigger)) {
                world.removeTileEntity(effectivePos);
                return true;
            }
        } else if (tile instanceof IProxedCursedTrigger) {
            // Tile is proxed, so before applying effects retrieve the inner trigger
            IProxedCursedTrigger proxedTrigger = (IProxedCursedTrigger)tile;
            return proxedTrigger.onTrigger(entity);
        }

        return false;
    }

    /** The core function that applies the trigger to a victim entity.
     * If the block or other class has custom checks or logic, can call this directly.
     * Returns true if the Trigger run out of curses (and TileEntity should be removed) */
    default boolean doApplyTrigger(Entity entity, TileEntityCursedTrigger trigger) {
        // Retrieve entity and perform checks
        if (entity == null || trigger == null) {
            return false;
        }
        // Perform trigger
        return trigger.applyToEntityAndDestroy(entity);
    }

    /** This method should return the position where the TileEntity is effectively placed/retrieved
     * for blocks that occupy multiple block spaces (doors for example).
     * If this function returns null, no trigger will be created */
    default BlockPos getEffectivePos(World world, BlockPos pos) {
        if (!isTriggerEnabled()) return null;
        return pos;
    }

    /** This method spawns particles at the specified position. Blocks can override to customize particle
     * spawning. This is useful if the effectivePos is far away from impact pos, etc */
    default void spawnParticles(World world, BlockPos impactPos, BlockPos effectivePos) {
        if (!isTriggerEnabled()) return;
        if (effectivePos == null || (!(world instanceof WorldServer)))
            return;
        WorldServer worldServer = (WorldServer)world;
        worldServer.spawnParticle(EnumParticleTypes.REDSTONE, false,
                effectivePos.getX() + 0.5, effectivePos.getY() + 0.5,  effectivePos.getZ() + 0.5,
                25, 0.5, 0.5, 0.5, 0.5);
    }

    boolean isTriggerEnabled();
}
