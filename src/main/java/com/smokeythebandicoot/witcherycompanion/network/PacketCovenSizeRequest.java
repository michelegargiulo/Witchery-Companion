package com.smokeythebandicoot.witcherycompanion.network;

import com.smokeythebandicoot.witcherycompanion.patches.coven.CovenSizeCheat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sent by the Coven tab of the Creative Medallion: asks to set the size of the coven of the player (or only to know it, with
 * CovenSizeCheat.QUERY). The server always answers with the size of the coven, as the client does not know it
 */
public class PacketCovenSizeRequest {

    public static class Message implements IMessage {

        private int hand;
        private int size;

        public Message() {}

        public Message(EnumHand hand, int size) {
            this.hand = hand.ordinal();
            this.size = size;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(hand);
            buf.writeInt(size);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            hand = buf.readByte();
            size = buf.readInt();
        }
    }

    public static class Handler implements IMessageHandler<Message, IMessage> {
        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                EntityPlayerMP player = context.getServerHandler().player;
                EnumHand[] hands = EnumHand.values();
                if (message.hand < 0 || message.hand >= hands.length) return;

                int size = CovenSizeCheat.handle(player, hands[message.hand], message.size);
                CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(new PacketCovenSizeResponse.Message(size, CovenSizeCheat.getMaxSize()), player);
            });
            return null;
        }
    }

}
