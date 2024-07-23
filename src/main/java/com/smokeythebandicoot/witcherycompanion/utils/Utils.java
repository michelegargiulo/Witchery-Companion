package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static void logChat(Object msg) {
        WitcheryCompanion.logger.info(msg);
        MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer().getServer();
        server.commandManager.executeCommand(server, "/say " + msg.toString());
    }

    public static void logChat(Collection<String> msgs) {
        for (String msg : msgs) {
            WitcheryCompanion.logger.info(msg);
            Utils.logChat(msg);
        }
    }

    public static List<String> printAltarPattern(World world, BlockAltar.AltarPatternMatch pattern) {
        if (pattern == null) return Collections.singletonList("-- NULL --");
        List<String> result = new ArrayList<>();
        for (BlockAltar.Part part : pattern.getParts().values()) {
            BlockPos pos = pattern.getParts().inverse().get(part);
            TileEntity te = world.getTileEntity(pos);
            TileEntityAltar altar = null;
            if (te instanceof TileEntityAltar)
                altar = (TileEntityAltar)te;

            StringBuilder sb = new StringBuilder();
            sb.append(part.getName()).append(" -> ").append(altar == null ? "NULL" : altar.isValid());
            result.add(sb.toString());
        }
        return result;
    }

    public static ItemStack blockstateToStack(IBlockState state) {
        return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
    }

    public static ItemStack blockstateToStack(IBlockState state, int amount) {
        return new ItemStack(state.getBlock(), amount, state.getBlock().getMetaFromState(state));
    }

    public static Block itemstackToBlock(ItemStack stack) {
        return Block.getBlockFromItem(stack.getItem());
    }

    public static IBlockState itemstackToBlockstate(ItemStack stack, Integer meta) {
        Block block = Block.getBlockFromItem(stack.getItem());
        return block.getStateFromMeta(meta == null ? stack.getMetadata() : meta);
    }
}
