package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import javax.annotation.Nullable;

/**
 * Why a spectator-based Crystal Ball divination ended, and what has to be done when it ends.
 * This enum must not reference any Minecraft type, so that it can be unit-tested in plain Java.
 */
public enum DivinationEndReason {

    /** The player released the Taglock Kit (or its max use duration elapsed) */
    STOPPED_USING(true, true, false, null),
    /** The player sneaked, which is the vanilla way of leaving a spectated entity */
    USER_EXIT(true, true, true, null),
    /** The target died, was unloaded, disconnected or left the dimension */
    TARGET_LOST(true, true, true, "witcherycompanion.message.divination.target_lost"),
    /** The body left next to the Crystal Ball was hurt */
    BODY_DAMAGED(true, true, true, "witcherycompanion.message.divination.body_damaged"),
    /** The body disappeared (killed by commands, removed by other mods...) */
    BODY_LOST(true, true, true, "witcherycompanion.message.divination.body_lost"),
    /** The player disconnected. No packets are sent, only server-side data is restored before it is saved */
    LOGOUT(true, true, false, null),
    /** The server is stopping. Players are restored before their data is saved */
    SERVER_STOP(true, true, false, null),
    /** The player changed dimension through other means. Game mode is restored, but the player is left where it is */
    DIMENSION_CHANGED(false, true, true, null),
    /** Someone changed the game mode of the player (/gamemode...). That choice is respected and not overwritten */
    EXTERNAL_GAMEMODE(true, false, true, null),
    /** Something went wrong while starting the divination, everything is rolled back */
    START_FAILED(true, true, false, null);

    private final boolean teleportHome;
    private final boolean restoreGameType;
    private final boolean stopActiveHand;
    private final String messageKey;

    DivinationEndReason(boolean teleportHome, boolean restoreGameType, boolean stopActiveHand, @Nullable String messageKey) {
        this.teleportHome = teleportHome;
        this.restoreGameType = restoreGameType;
        this.stopActiveHand = stopActiveHand;
        this.messageKey = messageKey;
    }

    /** True if the player must be moved back to where the divination started */
    public boolean teleportHome() {
        return teleportHome;
    }

    /** True if the game mode (and flying state) the player had before the divination must be restored */
    public boolean restoreGameType() {
        return restoreGameType;
    }

    /**
     * True if the active hand must be stopped. It is false when the end is triggered from inside the stop-using
     * hooks of the Taglock Kit, as the hand is already being stopped by the caller (avoids re-entrancy)
     */
    public boolean stopActiveHand() {
        return stopActiveHand;
    }

    /** Translation key of the message shown to the player, or null if the end is silent */
    @Nullable
    public String messageKey() {
        return messageKey;
    }

}
