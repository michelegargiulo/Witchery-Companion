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
    private UUID entityUuid;
    private boolean isDivining;

    private static final String DIVINATION_DATA_KEY = "DivinationData";

    public DivinationData() {
        posX = 0.0;
        posY = 0.0;
        posZ = 0.0;
        pitch = 0f;
        yaw = 0f;
        yawHead = 0f;
        gameType = GameType.SURVIVAL;
        entityUuid = null;
        isDivining = false;
    }

    public NBTTagCompound writeToNBT() {

        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound divinationTag = new NBTTagCompound();

        // Position
        NBTTagCompound posTag = new NBTTagCompound();
        posTag.setDouble("X", this.posX);
        posTag.setDouble("Y", this.posY);
        posTag.setDouble("Z", this.posZ);
        divinationTag.setTag("Position", posTag);

        // Rotation
        NBTTagCompound rotTag = new NBTTagCompound();
        rotTag.setFloat("Pitch", this.pitch);
        rotTag.setFloat("Yaw", this.yaw);
        rotTag.setFloat("YawHead", this.yawHead);
        divinationTag.setTag("Rotation", rotTag);

        // GameType
        divinationTag.setInteger("GameType", this.gameType.getID());

        // Divination
        divinationTag.setBoolean("IsDivining", this.isDivining);

        // StartTime and DivinatedEntity are not saved, as divination should be interrupted if player leaves,
        // thus they should not persist across world reloads or left and rejoining servers

        tag.setTag(DIVINATION_DATA_KEY, divinationTag);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {

        if (tag == null || !tag.hasKey(DIVINATION_DATA_KEY)) {
            return;
        }

        NBTTagCompound divinationData = tag.getCompoundTag(DIVINATION_DATA_KEY);

        // Position
        NBTTagCompound positionData = divinationData.getCompoundTag("Position");
        this.posX = positionData.getInteger("X");
        this.posY = positionData.getInteger("Y");
        this.posZ = positionData.getInteger("Z");

        // Rotation
        NBTTagCompound rotationData = divinationData.getCompoundTag("Rotation");
        this.pitch = rotationData.getFloat("Pitch");
        this.yaw = rotationData.getFloat("Yaw");
        this.yawHead = rotationData.getFloat("YawHead");

        // GameType
        this.gameType = GameType.getByID(divinationData.getInteger("GameType"));

        // Divination
        this.isDivining = divinationData.getBoolean("IsDivining");
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

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public boolean isDivining() {
        return isDivining;
    }

    public void setDivining(boolean divining) {
        isDivining = divining;
    }
}
