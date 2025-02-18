package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.player.IPlayerExtendedDataAccessor;
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

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;

        accessor.witcherycompanion$accessor$setDivinationData(data);
        playerEx.processSync();

        // Now set the player to spectator and spectate target entity
        player.setGameType(GameType.SPECTATOR);
        player.setSpectatingEntity(entity);

    }

    public static void terminateDivination(EntityPlayer player) {
        if (player == null) {
            return;
        }
        setPlayerFromData(player);
    }

    public static boolean isDivining(EntityPlayer player) {
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.witcherycompanion$accessor$getDivinationData();

        return divinationData != null && divinationData.getEntityUuid() != null;
    }

    public static void setPlayerFromData(EntityPlayer player) {

        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        DivinationData divinationData = accessor.witcherycompanion$accessor$getDivinationData();

        // No divination data, simply return
        if (divinationData == null) {
            return;
        }

        // Divination data found. May be leftovers or player was divining:
        if (divinationData.getEntityUuid() == null) {
            // Leftovers, do nothing
            return;
        }

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
        player.setGameType(divinationData.getGameType());

        // Reset divination data
        accessor.witcherycompanion$accessor$setDivinationData(new DivinationData());
        playerEx.processSync();
    }

    //TODO:
    // OnPlayerJoinWorld, check if player was divining. If it was, return to previous game mode

}
