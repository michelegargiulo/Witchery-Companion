package com.smokeythebandicoot.witcherycompanion.network.divination;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.network.progress.PacketWitcheryProgressResponse;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.utils.DiviningUtils;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

public class PacketWitcheryDivination {

    public static class Message implements IMessage {

        EntityPlayer serverPlayer;
        DivinationData data;

        public Message() {}

        public Message(EntityPlayer player) {
            this.serverPlayer = player;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(this.serverPlayer);
            IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
            DivinationData data = accessor.getDivinationData();

            NBTTagCompound tag;
            if (data == null) {
                tag = new NBTTagCompound();
            }
            else {
                tag = data.writeToNBT();
            }

            ByteBufUtils.writeTag(buf, tag);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            NBTTagCompound tag = ByteBufUtils.readTag(buf);
            this.data = new DivinationData();
            this.data.readFromNBT(tag);
        }

    }

    public static class Handler implements IMessageHandler<PacketWitcheryDivination.Message, IMessage> {

        @Override
        public IMessage onMessage(final PacketWitcheryDivination.Message message, final MessageContext context) {
            FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> {
                // Update local player status
                ClientProxy.updateLocalDivinationStatus(message.data);
            });
            return null;
        }
    }

}
