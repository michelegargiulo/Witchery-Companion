package com.smokeythebandicoot.witcherycompanion.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class Utils {

    public static void logChat(Object msg) {
        MinecraftServer server = Minecraft.getMinecraft().getIntegratedServer().getServer();
        server.commandManager.executeCommand(server, "/say " + msg.toString());
    }

}
