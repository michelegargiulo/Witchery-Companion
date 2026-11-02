package com.smokeythebandicoot.witcherycompanion.integrations.quark;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.rowandoor.IBlockRowanDoorAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.block.BlockRowanDoor;

/**
 Integration:
 [Bugfix] Fix Quark opening the other half of a Rowan Double Door, when the Player does not have the key. Rowan Doors
    check the key only in onBlockActivated, but Quark opens the other half from the RightClickBlock event by changing
    its state directly, so the key is never checked. The event is canceled before Quark can handle it
 */
public class RowanDoorIntegration {

    public static final RowanDoorIntegration INSTANCE = new RowanDoorIntegration();

    private RowanDoorIntegration() { }

    /** Runs on both sides: Quark also changes the door on the client, which would leave the door looking open.
     * A canceled event is also fired on the server by Forge, so both sides are consistent. Only doors that have
     * another Rowan Door next to them are considered, so that single doors keep their behaviour */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof BlockRowanDoor)) return;
        if (!hasAdjacentRowanDoor(world, pos)) return;

        IBlockRowanDoorAccessor door = (IBlockRowanDoorAccessor) state.getBlock();
        if (!door.witcherycompanion$accessor$hasKeyForDoor(world, pos, state, event.getEntityPlayer())) {
            event.setCanceled(true);
        }
    }

    /** Quark pairs a door with the one on its hinge side: a horizontal neighbour of the clicked door half */
    public static boolean hasAdjacentRowanDoor(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockRowanDoor) {
                return true;
            }
        }
        return false;
    }

}
