package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;


public class DiviningUtils {

    public static void startDivination(EntityPlayerMP player, EntityLivingBase entity) {

        // Error in the player or Entity cannot be spectated
        if (player == null || player.getServer() == null || !(entity instanceof EntityLivingBase)) {
            return;
        }
        WorldServer world = player.getServerWorld();

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
        data.setGameType(player.interactionManager.getGameType());
        data.setStartTime(world.getTotalWorldTime());
        data.setEntityUuid(entity.getUniqueID());
        data.setDivining(true);

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;

        accessor.setDivinationData(data);
        playerEx.processSync();

        // Now set the player to spectator and spectate target entity
        player.setGameType(GameType.SPECTATOR);
        player.setSpectatingEntity(entity);

    }

    public static void terminateDivination(EntityPlayerMP player) {
        if (player == null) {
            return;
        }
        setPlayerFromData(player);
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

    public static void setPlayerFromData(EntityPlayerMP player) {

        if (!(player instanceof EntityPlayerMP)) {
            return;
        }

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.getDivinationData();
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;

        // No divination data, simply return
        if (divinationData == null) {
            return;
        }

        // Divination data found. May be leftovers or player was divining:
        if (!divinationData.isDivining()) {
            // Leftovers, do nothing
            return;
        }

        // Reset divination data
        accessor.setDivinationData(new DivinationData()); // Important: isDivining is set to false in constructor
        playerEx.processSync();

        // Else, player was divining: reset player pos, rotation, gametype, etc. and remove data
        player.setPositionAndRotation(
                divinationData.getPosX(),
                divinationData.getPosY(),
                divinationData.getPosZ(),
                divinationData.getYaw(),
                divinationData.getPitch()
        );

        // Set rotation head and game type
        player.setRotationYawHead(divinationData.getYawHead());
        serverPlayer.setSpectatingEntity(serverPlayer);
        serverPlayer.setGameType(divinationData.getGameType());

        player.sendPlayerAbilities();
    }

}
