package com.smokeythebandicoot.witcherycompanion.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/** Client-side events of the spectator-based Crystal Ball divination. Only loaded on the client */
public class DivinationClientEvents {

    public static final DivinationClientEvents INSTANCE = new DivinationClientEvents();

    private DivinationClientEvents() { }

    /** Never carry the flag over to another world or server */
    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        DivinationClientState.setActive(false);
    }

}
