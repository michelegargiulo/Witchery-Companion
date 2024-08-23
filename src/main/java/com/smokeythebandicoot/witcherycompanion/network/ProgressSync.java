package com.smokeythebandicoot.witcherycompanion.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ProgressSync {

    // Server can send updated progress to clients. A message containing a full instance of IWitcheryProgress
    // is sent to the client(s) which request(s) updates
    public static void serverRequest(EntityPlayer player) {
        if (!player.world.isRemote) // Server-only
            CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(
                    new PacketWitcheryProgressResponse.Message(player), (EntityPlayerMP) player
            );
    }

    // Clients can request progress update from the server. An empty message is sent to the server, which
    // responds with a RESPONSE packet
    public static void clientRequest() {
        CompanionNetworkChannel.NETWORK_CHANNEL.sendToServer(
                new PacketWitcheryProgressRequest.Message()
        );
    }
}

