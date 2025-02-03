package com.smokeythebandicoot.witcherycompanion.api.player;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;

import java.util.UUID;

public class DivinationData {

    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private float yawHead;
    private GameType gameType;
    private long startTime;
    private UUID entityUuid; // Also act as a discriminant for whether the player was divining or not

    private static final String DIVINATION_DATA_KEY = "DivinationData";

    public DivinationData() {
        posX = 0.0;
        posY = 0.0;
        posZ = 0.0;
        pitch = 0f;
        yaw = 0f;
        yawHead = 0f;
        gameType = GameType.SURVIVAL;
        startTime = 0L;
        entityUuid = null;
    }

    public static void writeToNBT(DivinationData data, NBTTagCompound tag) {

        if (data == null) {
            return;
        }
        NBTTagCompound divinationTag = new NBTTagCompound();

        // Position
        NBTTagCompound posTag = new NBTTagCompound();
        posTag.setDouble("X", data.posX);
        posTag.setDouble("Y", data.posY);
        posTag.setDouble("Z", data.posZ);
        divinationTag.setTag("Position", posTag);

        // Rotation
        NBTTagCompound rotTag = new NBTTagCompound();
        rotTag.setFloat("Pitch", data.pitch);
        rotTag.setFloat("Yaw", data.yaw);
        rotTag.setFloat("YawHead", data.yawHead);
        rotTag.setTag("Rotation", rotTag);

        // GameType
        divinationTag.setInteger("GameType", data.gameType.getID());

        // StartTime and DivinatedEntity are not saved, as divination should be interrupted if player leaves,
        // thus they should not persist across world reloads or left and rejoining servers

        tag.setTag(DIVINATION_DATA_KEY, divinationTag);
    }

    public static DivinationData readFromNBT(NBTTagCompound tag) {

        if (tag == null || !tag.hasKey(DIVINATION_DATA_KEY)) {
            return null;
        }

        NBTTagCompound divinationData = tag.getCompoundTag(DIVINATION_DATA_KEY);
        DivinationData data = new DivinationData();

        // Position
        NBTTagCompound positionData = divinationData.getCompoundTag("Position");
        data.posX = positionData.getInteger("X");
        data.posX = positionData.getInteger("Y");
        data.posX = positionData.getInteger("Z");

        // Rotation
        NBTTagCompound rotationData = divinationData.getCompoundTag("Rotation");
        data.pitch = rotationData.getFloat("Pitch");
        data.yaw = rotationData.getFloat("Yaw");
        data.yawHead = rotationData.getFloat("YawHead");

        // GameType
        data.gameType = GameType.getByID(divinationData.getInteger("GameType"));

        return data;
    }

    public void setPositionFromVec3d(Vec3d vector) {
        posX = vector.x;
        posY = vector.y;
        posZ = vector.z;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYawHead() {
        return yawHead;
    }

    public void setYawHead(float yawHead) {
        this.yawHead = yawHead;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }
}
