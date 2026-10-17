package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * State of a running spectator-based Crystal Ball divination. Lives only in memory (see
 * {@link DivinationRegistry}), except for the small recovery tag in {@link DivinationRecovery}.
 */
public final class DivinationSession {

    public final UUID playerId;
    public final UUID targetId;
    public final UUID bodyId;
    public final int dimension;

    /** Game mode and flying state the player had before the divination, restored when it ends */
    public final GameType originalGameType;
    public final boolean originalFlying;

    /** Where the player was (and where the body was left) when the divination started */
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float pitch;

    /** Crystal Ball used to start the divination (for the cooldown), if known */
    @Nullable
    public final BlockPos ballPos;

    /** True once the client is known to have received a valid camera packet */
    public boolean cameraAttached;

    /** True if the player died during the divination. The session is kept until the player respawns */
    public boolean dead;

    public DivinationSession(UUID playerId, UUID targetId, UUID bodyId, int dimension, GameType originalGameType,
                             boolean originalFlying, double x, double y, double z, float yaw, float pitch,
                             @Nullable BlockPos ballPos) {
        this.playerId = playerId;
        this.targetId = targetId;
        this.bodyId = bodyId;
        this.dimension = dimension;
        this.originalGameType = originalGameType;
        this.originalFlying = originalFlying;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ballPos = ballPos;
    }

}
