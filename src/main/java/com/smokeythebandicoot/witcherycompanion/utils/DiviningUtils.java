package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import com.smokeythebandicoot.witcherycompanion.network.CompanionNetworkChannel;
import com.smokeythebandicoot.witcherycompanion.network.divination.PacketWitcheryDivination;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;


public class DiviningUtils {

    public static DivinationData getDivinationData(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        return accessor.getDivinationData();
    }

    public static void setDivinationData(EntityPlayer player, DivinationData data) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        accessor.setDivinationData(data);
    }

    public static void startDivination(EntityPlayer player, EntityLivingBase entity) {

        // Error in the player or Entity cannot be spectated
        if (player == null || player.world.isRemote || player.getServer() == null || !(entity instanceof EntityLivingBase)) {
            return;
        }

        EntityPlayerMP serverPlayer = (EntityPlayerMP)player;
        WorldServer world = serverPlayer.getServerWorld();

        // Player and Entity must share the same world
        if (world != entity.world) {
            return;
        }

        // Retrieve player info and create Divination Data
        DivinationData data = new DivinationData();

        data.setPositionFromVec3d(player.getPositionVector());
        data.setPitch(player.rotationPitch);
        data.setYaw(player.rotationYaw);
        data.setYawHead(player.getRotationYawHead());
        data.setGameType(serverPlayer.interactionManager.getGameType());
        data.setEntityUuid(entity.getUniqueID());
        data.setDivining(true);

        setDivinationData(player, data);

        // Now set the player to spectator and spectate target entity
        player.setGameType(GameType.SPECTATOR);
        serverPlayer.setSpectatingEntity(entity);

        // Update client with the new data
        Utils.logChat("divination start");
        CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(
                new PacketWitcheryDivination.Message(player), serverPlayer
        );

    }

    public static void terminateDivination(EntityPlayer player) {
        if (player == null || player.world.isRemote) {
            return;
        }

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.getDivinationData();

        // No divination data, simply return
        if (divinationData != null) {
            player.setPositionAndRotation(
                    divinationData.getPosX(),
                    divinationData.getPosY(),
                    divinationData.getPosZ(),
                    divinationData.getYaw(),
                    divinationData.getPitch()
            );

            // Set rotation head and game type
            player.setRotationYawHead(divinationData.getYawHead());
            ((EntityPlayerMP) player).setSpectatingEntity(player);
            player.setGameType(divinationData.getGameType());
        }

        // Reset divination data
        accessor.setDivinationData(new DivinationData()); // Important: isDivining is set to false in constructor

        Utils.logChat("divination end");
        // Update client for new data
        CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(
                new PacketWitcheryDivination.Message(player), (EntityPlayerMP) player
        );
    }

    public static boolean isDivining(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.getDivinationData();

        return divinationData != null && divinationData.isDivining();
    }

    public static Entity getDivinedEntity(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.getDivinationData();

        if (divinationData.getEntityUuid() == null || player.world.isRemote) {
            return null;
        }

        // World must be WorldServer and must be the same dimension, so
        // we can skip checking all dimensions
        WorldServer worldServer = (WorldServer) player.world;

        return worldServer.getEntityFromUuid(divinationData.getEntityUuid());
    }

}
