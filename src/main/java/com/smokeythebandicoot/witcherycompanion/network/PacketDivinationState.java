package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.client.DivinationClientState;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Server -> client. Tells the client that the player started (or stopped) a spectator-based Crystal Ball divination,
 * so that the client can hide what would reveal the position of the target (F3 debug screen)
 */
public class PacketDivinationState {

    public static class Message implements IMessage {

        boolean active;

        public Message() {}

        public Message(boolean active) {
            this.active = active;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(active);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            active = buf.readBoolean();
        }
    }

    // This is the client's handling of the information
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            final boolean active = message.active;
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> DivinationClientState.setActive(active));
            return null;
        }
    }
}
