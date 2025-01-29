package com.smokeythebandicoot.witcherycompanion.network.divination;

import com.smokeythebandicoot.witcherycompanion.network.CompanionNetworkChannel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class DivinationSync {

    public static void updateDivination(EntityPlayer player) {
        if (!player.world.isRemote) {
            CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(
                    new PacketWitcheryDivination.Message(player), (EntityPlayerMP) player
            );
        }
    }

}
