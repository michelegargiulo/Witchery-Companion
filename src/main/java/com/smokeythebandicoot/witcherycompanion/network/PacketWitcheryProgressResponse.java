package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.api.capability.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.capability.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.smokeythebandicoot.witcherycompanion.api.capability.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

public class PacketWitcheryProgressResponse {

    public static class Message implements IMessage {

        // Server vars only
        EntityPlayer serverPlayer;

        // Client vars only
        IWitcheryProgress clientProgress;

        public Message() {}

        // Message data is passed along from server
        public Message(EntityPlayer player) {
            serverPlayer = player; // Get server player
        }

        // Then serialized into bytes (on server)
        @Override
        public void toBytes(ByteBuf buf) {
            // Loop through nutrients from server player, and add to buffer
            clientProgress = serverPlayer.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);

            // Write unlocked secrets
            for (String unlockedSecret : clientProgress.getUnlockedProgress()) {
                ByteBufUtils.writeUTF8String(buf, unlockedSecret);
            }
        }

        // Then deserialized (on the client)
        @Override
        public void fromBytes(ByteBuf buf) {
            // Loop through buffer stream to build nutrition data
            clientProgress = new WitcheryProgress();
            while(buf.isReadable()) {
                String unlockedSecret = ByteBufUtils.readUTF8String(buf);
                clientProgress.unlockProgress(unlockedSecret);
            }
        }
    }

    // Message Handler Subclass
    // This is the client's handling of the information
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                // Update local progress data. Client proxy performs null check and eventual reload
                ClientProxy.updateLocalWitcheryProgress(message.clientProgress);
            });
            return null;
        }
    }
}
