package com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger;

import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface ICursableTrigger {

    /** This method is responsible for containing recurring code for activating the trigger
     * Checks if the Block at 'pos' has a TileEntity and is a TileEntityCursedTrigger.
     * After the checks, applies the trigger. Eventually removes the TileEntity if
     * the trigger run out of curses. Returns true if the effect has been applied */
    default boolean onTrigger(World world, BlockPos pos, Entity entity) {

        // Retrieve effectivePos for the specific implementation (Block-specific)
        BlockPos effectivePos = this.getEffectivePos(world, pos);

        // Retrieve TE and perform checks
        TileEntity tile = world.getTileEntity(effectivePos);

        if (tile instanceof TileEntityCursedTrigger) {
            // Retrieve tile and apply effect
            TileEntityCursedTrigger trigger = (TileEntityCursedTrigger) tile;
            if (this.doApplyTrigger(entity, trigger)) {
                world.removeTileEntity(effectivePos);
                return true;
            }
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
     * for blocks that occupy multiple block spaces (doors for example) */
    @Nonnull
    default BlockPos getEffectivePos(World world, BlockPos pos) {
        return pos;
    }

}
