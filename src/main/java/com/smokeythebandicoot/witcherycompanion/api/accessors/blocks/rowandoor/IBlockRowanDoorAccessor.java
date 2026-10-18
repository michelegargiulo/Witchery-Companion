package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.rowandoor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockRowanDoorAccessor {

    /** Checks if the Player holds, or has in the inventory, a Door Key (or a Keyring with a Door Key) that opens the door
     * at the given position. This is the same check W:R does when the door is right-clicked. It only reads the world
     * and the Player inventory, so it can be called on both sides */
    boolean witcherycompanion$accessor$hasKeyForDoor(World world, BlockPos pos, IBlockState state, EntityPlayer player);

}
