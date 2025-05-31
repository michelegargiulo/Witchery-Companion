package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

public class PacketWitcheryProgressResponse {

    public static class Message implements IMessage {

        // Server vars only
        EntityPlayer serverPlayer;

        // Client vars only
        IWitcheryProgress progress;

        public Message() {}

        // Message data is passed along from server
        public Message(EntityPlayer player) {
            serverPlayer = player; // Get server player
        }

        // Then serialized into bytes (on server)
        @Override
        public void toBytes(ByteBuf buf) {
            progress = serverPlayer.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);

            // Write unlocked secrets
            for (String unlockedSecret : progress.getUnlockedProgress()) {
                ByteBufUtils.writeUTF8String(buf, unlockedSecret);
            }
        }

        // Then deserialized (on the client)
        @Override
        public void fromBytes(ByteBuf buf) {
            // Loop through buffer stream to build nutrition data
            progress = new WitcheryProgress();
            while(buf.isReadable()) {
                String unlockedSecret = ByteBufUtils.readUTF8String(buf);
                progress.unlockProgress(unlockedSecret);
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
                ClientProxy.updateLocalWitcheryProgress(message.progress);
            });
            return null;
        }
    }
}
