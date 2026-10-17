package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Small tag stored in the persistent data of the player ({@link EntityPlayer#PERSISTED_NBT_TAG}) for the whole
 * duration of a divination, and removed when it ends. Divination is NOT resumed after a relog: this tag only exists
 * so that, if the server crashes (or the player data is auto-saved) while somebody is divining, the player does not
 * come back stuck in Spectator mode at the target's position: on login, game mode and position are restored.
 */
public final class DivinationRecovery {

    private static final String KEY = "witcherycompanion_divination";

    private DivinationRecovery() { }

    /** Data read back from the recovery tag */
    public static final class Data {
        public final GameType gameType;
        public final boolean flying;
        public final int dimension;
        public final double x;
        public final double y;
        public final double z;
        public final float yaw;
        public final float pitch;
        @Nullable
        public final UUID bodyId;

        Data(GameType gameType, boolean flying, int dimension, double x, double y, double z, float yaw, float pitch,
             @Nullable UUID bodyId) {
            this.gameType = gameType;
            this.flying = flying;
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.bodyId = bodyId;
        }
    }

    public static void write(EntityPlayer player, DivinationSession session) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("gameType", session.originalGameType.getID());
        tag.setBoolean("flying", session.originalFlying);
        tag.setInteger("dimension", session.dimension);
        tag.setDouble("x", session.x);
        tag.setDouble("y", session.y);
        tag.setDouble("z", session.z);
        tag.setFloat("yaw", session.yaw);
        tag.setFloat("pitch", session.pitch);
        tag.setUniqueId("body", session.bodyId);

        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persisted = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        persisted.setTag(KEY, tag);
        data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    @Nullable
    public static Data read(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) return null;
        NBTTagCompound persisted = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(KEY, 10)) return null;
        NBTTagCompound tag = persisted.getCompoundTag(KEY);

        GameType type = GameType.getByID(tag.getInteger("gameType"));
        // Never "restore" into Spectator or into an unset mode: Survival is the safe fallback
        if (type == GameType.SPECTATOR || type == GameType.NOT_SET) type = GameType.SURVIVAL;
        UUID body = tag.hasUniqueId("body") ? tag.getUniqueId("body") : null;
        return new Data(type, tag.getBoolean("flying"), tag.getInteger("dimension"),
                tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"),
                tag.getFloat("yaw"), tag.getFloat("pitch"), body);
    }

    public static boolean has(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        return data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)
                && data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey(KEY, 10);
    }

    public static void clear(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) return;
        data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).removeTag(KEY);
    }

}
