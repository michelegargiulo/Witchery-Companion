package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;

import java.util.*;

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

    public static List<Block> getBlocksForOre(String name) {
        List<Block> blockList = new ArrayList<>();
        NonNullList<ItemStack> items = OreDictionary.getOres(name);
        for (ItemStack item : items) {
            blockList.add(Block.getBlockFromItem(item.getItem()));
        }
        return blockList;
    }

    public static void logException(String message, Throwable t) {
        WitcheryCompanion.logger.error(message);
        WitcheryCompanion.logger.error(t.getMessage());
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement stackTraceElem : t.getStackTrace()) {
            builder.append("\t at ").append(stackTraceElem.toString()).append("\n");
        }
        WitcheryCompanion.logger.error(builder.toString());
    }

    public static ResourceLocation generateRandomRecipeId(String pathPrefix) {
        return new ResourceLocation(WitcheryCompanion.MODID, pathPrefix + UUID.randomUUID().toString().replace("-", ""));
    }



}
