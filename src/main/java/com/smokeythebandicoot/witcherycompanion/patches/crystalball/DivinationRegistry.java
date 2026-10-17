package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the running divinations. This class deliberately references no Witchery: Resurrected class, so that it can
 * be called from early Mixins (vanilla classes). Maps are concurrent because packet handlers may query them
 * from the network thread before the packet is re-queued on the server thread.
 */
public final class DivinationRegistry {

    private static final Map<UUID, DivinationSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockPos> PENDING_BALLS = new ConcurrentHashMap<>();
    private static final Map<UUID, PendingResync> PENDING_RESYNCS = new ConcurrentHashMap<>();

    private DivinationRegistry() { }

    /** Remaining ticks before the game mode of a player is verified, and the game mode it must have */
    public static final class PendingResync {
        public int ticksLeft;
        public final int gameTypeId;

        public PendingResync(int ticksLeft, int gameTypeId) {
            this.ticksLeft = ticksLeft;
            this.gameTypeId = gameTypeId;
        }
    }

    public static boolean isDiving(UUID playerId) {
        return playerId != null && SESSIONS.containsKey(playerId);
    }

    public static boolean isEmpty() {
        return SESSIONS.isEmpty() && PENDING_RESYNCS.isEmpty();
    }

    @Nullable
    public static DivinationSession get(UUID playerId) {
        return SESSIONS.get(playerId);
    }

    @Nullable
    public static DivinationSession getByBody(UUID bodyId) {
        for (DivinationSession session : SESSIONS.values()) {
            if (session.bodyId.equals(bodyId)) return session;
        }
        return null;
    }

    public static boolean hasBody(UUID bodyId) {
        return getByBody(bodyId) != null;
    }

    public static void put(DivinationSession session) {
        SESSIONS.put(session.playerId, session);
    }

    @Nullable
    public static DivinationSession remove(UUID playerId) {
        return SESSIONS.remove(playerId);
    }

    public static Iterable<DivinationSession> all() {
        return SESSIONS.values();
    }

    public static void setPendingBall(UUID playerId, BlockPos pos) {
        PENDING_BALLS.put(playerId, pos);
    }

    @Nullable
    public static BlockPos takePendingBall(UUID playerId) {
        return PENDING_BALLS.remove(playerId);
    }

    public static void scheduleResync(UUID playerId, int gameTypeId) {
        PENDING_RESYNCS.put(playerId, new PendingResync(DivinationRules.RESYNC_DELAY_TICKS, gameTypeId));
    }

    @Nullable
    public static PendingResync getPendingResync(UUID playerId) {
        return PENDING_RESYNCS.get(playerId);
    }

    public static void clearPendingResync(UUID playerId) {
        PENDING_RESYNCS.remove(playerId);
    }

    /** Drops every transient state of a player (on logout) */
    public static void forgetPlayer(UUID playerId) {
        SESSIONS.remove(playerId);
        PENDING_BALLS.remove(playerId);
        PENDING_RESYNCS.remove(playerId);
    }

    /** Drops everything (server stopped) */
    public static void clearAll() {
        SESSIONS.clear();
        PENDING_BALLS.clear();
        PENDING_RESYNCS.clear();
    }

}
