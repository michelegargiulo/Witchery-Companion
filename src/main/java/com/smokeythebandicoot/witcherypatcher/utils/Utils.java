package com.smokeythebandicoot.witcherypatcher.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

public class Utils {

    public static void logChat(Object msg) {
        MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer().getServer();
        server.commandManager.executeCommand(server, "/say " + msg.toString());
    }

}
