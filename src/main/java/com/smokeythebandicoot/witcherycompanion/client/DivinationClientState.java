package com.smokeythebandicoot.witcherycompanion.client;

import net.minecraft.client.Minecraft;

/**
 * Client-side state of the spectator-based Crystal Ball divination. Only loaded on the client.
 * The flag is driven by the server (see PacketDivinationState), and is deliberately NOT affected by the client config:
 * what matters is that the server is running a divination for this player.
 */
public final class DivinationClientState {

    private static volatile boolean active = false;

    private DivinationClientState() { }

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active;
    }

    /**
     * The debug screen (F3) shows the coordinates of the camera, which during a divination is the target.
     * It is hidden only if the player is really in Spectator mode on the client: a stale flag cannot affect
     * a player that is not spectating.
     */
    public static boolean shouldHideDebugScreen() {
        if (!active) return false;
        Minecraft mc = Minecraft.getMinecraft();
        return mc.playerController != null && mc.playerController.isSpectatorMode();
    }

}
