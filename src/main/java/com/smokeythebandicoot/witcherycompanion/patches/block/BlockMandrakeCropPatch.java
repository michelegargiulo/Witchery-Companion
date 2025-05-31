package com.smokeythebandicoot.witcherycompanion.patches.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.mandrakecrop.IBlockMandrakeCropAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.block.BlockMandrakeCrop;
import slimeknights.tconstruct.library.events.TinkerToolEvent;

public class BlockMandrakeCropPatch {

    @SubscribeEvent
    public static void onScytheHarvest(TinkerToolEvent.OnScytheHarvest event) {

        // Not a Mandrake crop
        if (event.blockState == null || !(event.blockState.getBlock() instanceof BlockMandrakeCrop)) {
            return;
        }

        IBlockMandrakeCropAccessor invoker = (IBlockMandrakeCropAccessor) event.blockState.getBlock();

        // Gather event params
        World world = event.world;
        EntityPlayer player = event.player;
        BlockPos pos = event.pos;
        IBlockState state = event.blockState;
        ItemStack stack = event.itemStack;

        // If Mandrake is spawned, seeds should not be replanted, so cancel this event
        if (invoker.witcherycompanion$accessor$shouldSpawnMandrake(world, player, pos, state, stack)) {
            invoker.witcherycompanion$accessor$spawnMandrake(world, player, pos, state, stack);
            event.setResult(Event.Result.DENY);
            world.setBlockToAir(pos);
            return;
        }

        event.setResult(Event.Result.ALLOW);

    }

}
