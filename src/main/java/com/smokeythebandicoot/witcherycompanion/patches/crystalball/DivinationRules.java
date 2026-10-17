package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import javax.annotation.Nullable;

/**
 * Pure decision logic of the spectator-based Crystal Ball divination.
 * It does not reference any Minecraft type, so that it can be unit-tested in plain Java.
 */
public final class DivinationRules {

    /** Ticks after the end of a divination after which the game mode is verified and re-sent to the client */
    public static final int RESYNC_DELAY_TICKS = 2;

    private DivinationRules() { }

    /**
     * Decides whether a running divination must end this tick. Checks are ordered by priority:
     * the first one that is true wins.
     * @return the reason to end the divination, or null if it can go on
     */
    @Nullable
    public static DivinationEndReason evaluateTick(boolean dimensionChanged, boolean notSpectatorAnymore,
                                                   boolean targetLost, boolean notUsingKit,
                                                   boolean leftSpectatedEntity, boolean bodyLost) {
        if (dimensionChanged) return DivinationEndReason.DIMENSION_CHANGED;
        if (notSpectatorAnymore) return DivinationEndReason.EXTERNAL_GAMEMODE;
        if (targetLost) return DivinationEndReason.TARGET_LOST;
        if (notUsingKit) return DivinationEndReason.STOPPED_USING;
        if (leftSpectatedEntity) return DivinationEndReason.USER_EXIT;
        if (bodyLost) return DivinationEndReason.BODY_LOST;
        return null;
    }

    /**
     * The body stays next to the Crystal Ball, which can be far from the target: its chunk can legitimately be
     * unloaded while the player is away. A body that is not found is "lost" only if its chunk is loaded.
     */
    public static boolean isBodyLost(boolean bodyFound, boolean bodyDead, boolean homeChunkLoaded) {
        if (bodyFound) return bodyDead;
        return homeChunkLoaded;
    }

    /**
     * The client ignores the camera packet of an entity it does not know yet. The packet is therefore sent
     * again, once, as soon as the target is tracked by the player (the spawn packet of the target is sent
     * before it on the same connection, so the client knows the entity when it receives it).
     */
    public static boolean shouldAttachCamera(boolean alreadyAttached, boolean targetPresent, boolean trackedByPlayer) {
        return !alreadyAttached && targetPresent && trackedByPlayer;
    }

    /** Vanilla game mode ids: -1 NOT_SET, 0 SURVIVAL, 1 CREATIVE, 2 ADVENTURE, 3 SPECTATOR */
    public static boolean canStartFromGameType(int gameTypeId) {
        return gameTypeId >= 0 && gameTypeId <= 2;
    }

    /** Divination cannot start if the target already is the camera of another divination (avoids loops) */
    public static boolean canTarget(boolean targetIsSelf, boolean targetAlive, boolean sameWorld, boolean targetIsDiving) {
        return !targetIsSelf && targetAlive && sameWorld && !targetIsDiving;
    }

}
