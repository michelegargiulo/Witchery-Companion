package com.smokeythebandicoot.witcherycompanion.utils;

/**
 * Pure geometry of the Coven Witches around a circle during the coven summoning stage of a rite. Witchery: Resurrected
 * only defines the positions of the first {@link #BASE_SLOTS} witches (a fixed array). Witches with a higher index are
 * placed on concentric rings around the circle, each witch looking towards the center of the circle.
 * No Minecraft types are used here, so that the logic can be unit-tested.
 */
public final class CovenCircleLayout {

    /** Number of witches that Witchery: Resurrected has a position for */
    public static final int BASE_SLOTS = 6;

    /** Maximum number of witches that can be placed. The coven size of a ritual is stored as a byte in its NBT */
    public static final int MAX_SLOTS = 127;

    /** Number of witches on each additional ring */
    public static final int SLOTS_PER_RING = 8;

    /** Radius (in blocks) of the first additional ring, each next ring is one block farther */
    public static final int FIRST_RING_RADIUS = 4;

    private CovenCircleLayout() {}

    /** X offset from the circle center of the witch with the given index (>= BASE_SLOTS) */
    public static int offsetX(int index) {
        double angle = angle(index);
        return (int) Math.round(radius(index) * Math.cos(angle));
    }

    /** Z offset from the circle center of the witch with the given index (>= BASE_SLOTS) */
    public static int offsetZ(int index) {
        double angle = angle(index);
        return (int) Math.round(radius(index) * Math.sin(angle));
    }

    /** Rotation yaw (Minecraft convention: 0 = looking towards +Z) of a witch standing at the given offset from the
     * center, looking towards the center */
    public static float yawTowardsCenter(int offsetX, int offsetZ) {
        return (float) Math.toDegrees(Math.atan2(offsetX, -offsetZ));
    }

    /** Rotation yaw of the witch with the given index (>= BASE_SLOTS) */
    public static float yaw(int index) {
        return yawTowardsCenter(offsetX(index), offsetZ(index));
    }

    private static int radius(int index) {
        return FIRST_RING_RADIUS + (index - BASE_SLOTS) / SLOTS_PER_RING;
    }

    /** Slots of a ring are evenly spaced, and the ring is rotated half a step so that no witch stands on an axis */
    private static double angle(int index) {
        int slot = (index - BASE_SLOTS) % SLOTS_PER_RING;
        return Math.toRadians((slot + 0.5) * (360.0 / SLOTS_PER_RING));
    }

}
