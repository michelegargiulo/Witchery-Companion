package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.network.divination.PacketWitcheryDivination;
import com.smokeythebandicoot.witcherycompanion.network.progress.PacketWitcheryProgressRequest;
import com.smokeythebandicoot.witcherycompanion.network.progress.PacketWitcheryProgressResponse;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CompanionNetworkChannel {

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(WitcheryCompanion.MODID);

    // Message IDs
    private final static int MESSAGE_WITCHERY_PROGRESS_REQUEST = 0;
    private final static int MESSAGE_WITCHERY_PROGRESS_RESPONSE = 1;
    private final static int MESSAGE_WITCHERY_DIVINATION_UPDATE = 2;

    // Register messages on run
    public static void registerMessages() {
        NETWORK_CHANNEL.registerMessage(PacketWitcheryProgressRequest.Handler.class, PacketWitcheryProgressRequest.Message.class, MESSAGE_WITCHERY_PROGRESS_REQUEST, Side.SERVER);
        NETWORK_CHANNEL.registerMessage(PacketWitcheryProgressResponse.Handler.class, PacketWitcheryProgressResponse.Message.class, MESSAGE_WITCHERY_PROGRESS_RESPONSE, Side.CLIENT);
        NETWORK_CHANNEL.registerMessage(PacketWitcheryDivination.Handler.class, PacketWitcheryDivination.Message.class, MESSAGE_WITCHERY_DIVINATION_UPDATE, Side.CLIENT);
    }
}
