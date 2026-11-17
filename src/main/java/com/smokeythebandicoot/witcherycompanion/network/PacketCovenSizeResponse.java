package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.client.gui.CovenSizeMedallionEntry;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Sent to the client with the size of its coven and the maximum size, to show them in the Coven tab of the Creative Medallion */
public class PacketCovenSizeResponse {

    public static class Message implements IMessage {

        private int size;
        private int max;

        public Message() {}

        public Message(int size, int max) {
            this.size = size;
            this.max = max;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(size);
            buf.writeInt(max);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            size = buf.readInt();
            max = buf.readInt();
        }
    }

    public static class Handler implements IMessageHandler<Message, IMessage> {
        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                CovenSizeMedallionEntry.update(message.size, message.max);
            });
            return null;
        }
    }

}
