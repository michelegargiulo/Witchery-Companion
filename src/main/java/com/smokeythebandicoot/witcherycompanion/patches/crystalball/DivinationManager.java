package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.network.CompanionNetworkChannel;
import com.smokeythebandicoot.witcherycompanion.network.PacketDivinationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.msrandom.witchery.block.entity.TileEntityCrystalBall;
import net.msrandom.witchery.entity.EntitySleepingBody;
import net.msrandom.witchery.item.ItemTaglockKit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Server-side logic of the spectator-based Crystal Ball divination.
 *
 * Flow: the player right-clicks a Crystal Ball with a bound Taglock Kit (Witchery: Resurrected) and keeps the button
 * pressed. On the first tick of the use, the player is put in Spectator mode and made to spectate the target, and an
 * Witchery sleeping body is left where the player was. The divination ends (see {@link DivinationEndReason}) when
 * the kit is released, the target is lost, the body is hurt or lost, the player sneaks, disconnects, dies...
 *
 * Game mode changes ALWAYS go through {@link EntityPlayerMP#setGameType}: it is the only method that updates the
 * server, the client controller (SPacketChangeGameState), the client tab-list (SPacketPlayerListItem) and the
 * abilities. {@code interactionManager.setGameType} alone only updates the server and the tab-list, which leaves
 * the client controller with the old game mode (the survival-on-client, spectator-on-server desync).
 */
public final class DivinationManager {

    /** Entity data flag set on the bodies spawned by divinations, so that orphans can be recognised and removed */
    public static final String BODY_TAG = "witcherycompanion_divination_body";

    private DivinationManager() { }

    // ------------------------------------------------------------------------------------------------------------
    // Entry points called by Mixins
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Called before Crystal Ball power is consumed by a Taglock Kit. Checks the cooldown of the Crystal Ball and
     * remembers which one is being used.
     * @return false if the divination attempt must be refused (cooldown), true otherwise
     */
    public static boolean beginDivinationAttempt(World world, EntityPlayer player, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityCrystalBall && !((TileEntityCrystalBall) tile).canBeUsed()) {
            player.sendMessage(new TextComponentTranslation("prediction.witchery.recharging")
                    .setStyle(new Style().setColor(TextFormatting.GRAY)));
            return false;
        }
        DivinationRegistry.setPendingBall(player.getUniqueID(), pos.toImmutable());
        return true;
    }

    /** Called after the power of a Crystal Ball was consumed: the cooldown of the Crystal Ball starts */
    public static void markCrystalBallUsed(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityCrystalBall) ((TileEntityCrystalBall) tile).onUsed();
    }

    /**
     * Called every tick the Taglock Kit is being used by a player.
     * @param elapsed ticks since the use started (0 on the first tick)
     * @return true if the original Witchery logic (client-side camera) must be skipped for this tick
     */
    public static boolean onUsingTick(EntityPlayerMP player, ItemStack stack, int elapsed) {
        if (DivinationRegistry.isDiving(player.getUniqueID())) return true;
        if (elapsed != 0) return false;

        // Same checks Witchery does: a bound entity, in the same dimension. Divining on yourself is left untouched
        EntityLivingBase bound = ItemTaglockKit.getBoundEntity(player.world, stack, 0);
        if (bound == null || bound == player || bound.dimension != player.dimension) return false;
        return tryStart(player, bound);
    }

    /** Called when the Taglock Kit stops being used (released or max duration elapsed) */
    public static void onUseEnded(EntityPlayerMP player) {
        DivinationSession session = DivinationRegistry.get(player.getUniqueID());
        if (session != null && !session.dead) end(player, DivinationEndReason.STOPPED_USING, null, 0f);
    }

    /**
     * Called when the body of a divination is attacked.
     * @return true if the attack has been handled (and Witchery must ignore it)
     */
    public static boolean onBodyAttacked(EntitySleepingBody body, DamageSource source, float amount) {
        if (body.world.isRemote || !body.getEntityData().getBoolean(BODY_TAG)) return false;
        DivinationSession session = DivinationRegistry.getByBody(body.getUniqueID());
        if (session == null) return false;
        if (session.dead) return true;

        MinecraftServer server = body.world.getMinecraftServer();
        EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUUID(session.playerId);
        if (player == null) return false;

        // Creative players can still remove bodies by punching them, without hurting the owner
        Entity attacker = source.getImmediateSource();
        boolean adminRemoval = attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode;
        end(player, DivinationEndReason.BODY_DAMAGED, adminRemoval ? null : source, amount);
        return true;
    }

    // ------------------------------------------------------------------------------------------------------------
    // Start
    // ------------------------------------------------------------------------------------------------------------

    /** @return true if a divination is now running for this player */
    public static boolean tryStart(EntityPlayerMP player, EntityLivingBase target) {
        if (!ModConfig.PatchesConfiguration.BlockTweaks.crystalBall_tweakSpectatorDivination) return false;
        UUID playerId = player.getUniqueID();
        if (DivinationRegistry.isDiving(playerId)) return true;

        boolean targetDiving = target instanceof EntityPlayer && DivinationRegistry.isDiving(target.getUniqueID());
        if (!DivinationRules.canTarget(target == player, target.isEntityAlive(), target.world == player.world, targetDiving))
            return false;

        GameType originalType = player.interactionManager.getGameType();
        if (player.isDead || player.isPlayerSleeping() || !DivinationRules.canStartFromGameType(originalType.getID()))
            return false;

        // Dismount first (SPECTATOR does it anyway), so that the saved position is the one the player will keep
        player.dismountRidingEntity();

        WorldServer world = player.getServerWorld();
        boolean targetKnownByClient = world.getEntityTracker().getTrackingPlayers(target).contains(player);

        // Body: same thing Witchery spawns when the player walks in the Spirit World
        EntitySleepingBody body = new EntitySleepingBody(world);
        body.setHealth(player.getHealth());
        body.setCustomNameTag(player.getName());
        body.setOwner(player);
        body.setLocationAndAngles(0.5D + MathHelper.floor(player.posX), player.posY,
                0.5D + MathHelper.floor(player.posZ), player.rotationYaw, 0.0F);
        body.getEntityData().setBoolean(BODY_TAG, true);

        DivinationSession session = new DivinationSession(playerId, target.getUniqueID(), body.getUniqueID(),
                player.dimension, originalType, player.capabilities.isFlying,
                player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch,
                DivinationRegistry.takePendingBall(playerId));
        session.cameraAttached = targetKnownByClient;

        // The session must be registered BEFORE the body joins the world, or it would be removed as an orphan
        DivinationRegistry.put(session);
        if (!world.spawnEntity(body)) {
            DivinationRegistry.remove(playerId);
            return false;
        }
        // Written after body.setOwner(), which snapshots the player data, so the tag is not copied in the body
        DivinationRecovery.write(player, session);

        try {
            player.setGameType(GameType.SPECTATOR);
            if (!player.isSpectator()) throw new IllegalStateException("Player did not enter Spectator mode");
            player.setSpectatingEntity(target);
        } catch (Exception e) {
            WitcheryCompanion.logger.error("[Crystal Ball] Failed to start divination for {}, rolling back",
                    player.getName(), e);
            end(player, DivinationEndReason.START_FAILED, null, 0f);
            return false;
        }

        notifyClient(player, true);
        return true;
    }

    // ------------------------------------------------------------------------------------------------------------
    // End
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Ends the divination of a player, whatever the reason, and puts everything back as it was. The session is
     * removed first, so that this method is safe to call again (or from hooks fired by what it does).
     * @param damageSource if not null, the damage is dealt to the player after the original game mode is back
     */
    public static void end(EntityPlayerMP player, DivinationEndReason reason, @Nullable DamageSource damageSource, float damage) {
        DivinationSession session = DivinationRegistry.remove(player.getUniqueID());
        if (session == null) return;
        MinecraftServer server = player.server;

        // 1. Most important: game mode and abilities
        if (reason.restoreGameType()) {
            try {
                restoreGameType(player, session);
            } catch (Exception e) {
                WitcheryCompanion.logger.error("[Crystal Ball] Failed to restore game mode of {}", player.getName(), e);
            }
        }

        // 2. Back to the Crystal Ball
        if (reason.teleportHome()) {
            try {
                teleportHome(player, session, reason != DivinationEndReason.LOGOUT);
            } catch (Exception e) {
                WitcheryCompanion.logger.error("[Crystal Ball] Failed to teleport {} back", player.getName(), e);
            }
        }

        // 3. Persistent tag: from now on nothing needs to be recovered
        DivinationRecovery.clear(player);

        // 4. Everything else is best-effort
        try {
            removeBody(server, session.bodyId);
            if (reason.stopActiveHand() && player.isHandActive()) player.stopActiveHand();
            if (reason != DivinationEndReason.LOGOUT) notifyClient(player, false);
            if (session.ballPos != null) markCrystalBallUsed(player.world, session.ballPos);
            if (reason.messageKey() != null) {
                player.sendStatusMessage(new TextComponentTranslation(reason.messageKey()), true);
            }
        } catch (Exception e) {
            WitcheryCompanion.logger.error("[Crystal Ball] Failed to complete end of divination of {}", player.getName(), e);
        }

        if (reason.restoreGameType() && reason != DivinationEndReason.LOGOUT) {
            DivinationRegistry.scheduleResync(player.getUniqueID(), session.originalGameType.getID());
        }

        // 5. The damage that hurt the body goes to the player, who is now back in the original game mode
        if (damageSource != null && damage > 0f && !player.isDead) {
            player.attackEntityFrom(damageSource, damage);
        }
    }

    /** Ends all the divinations (the server is stopping): player data is going to be saved */
    public static void endAll(MinecraftServer server) {
        List<DivinationSession> sessions = new ArrayList<>();
        for (DivinationSession session : DivinationRegistry.all()) sessions.add(session);
        for (DivinationSession session : sessions) {
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(session.playerId);
            if (player == null) DivinationRegistry.remove(session.playerId);
            else if (session.dead) finishAfterDeath(player);
            else end(player, DivinationEndReason.SERVER_STOP, null, 0f);
        }
    }

    private static void restoreGameType(EntityPlayerMP player, DivinationSession session) {
        // Full path: server + client controller + tab-list + abilities. Also makes the player spectate itself again
        player.setGameType(session.originalGameType);
        // Spectator forces flying: put back what the player had (Creative players keep flying only if they were)
        player.capabilities.isFlying = session.originalFlying && player.capabilities.allowFlying;
        player.sendPlayerAbilities();
    }

    private static void teleportHome(EntityPlayerMP player, DivinationSession session, boolean sendPacket) {
        if (sendPacket && player.connection != null) {
            player.connection.setPlayerLocation(session.x, session.y, session.z, session.yaw, session.pitch);
            // Move the chunks watched by the player right away, instead of waiting for the next movement packet
            player.server.getPlayerList().serverUpdateMovingPlayer(player);
        } else {
            player.setPositionAndRotation(session.x, session.y, session.z, session.yaw, session.pitch);
        }
        player.fallDistance = 0f;
        player.motionX = 0D;
        player.motionY = 0D;
        player.motionZ = 0D;
    }

    private static void removeBody(MinecraftServer server, UUID bodyId) {
        if (bodyId == null) return;
        for (WorldServer world : server.worlds) {
            if (world == null) continue;
            Entity body = world.getEntityFromUuid(bodyId);
            // setDead (and not removeEntity): this can run while entities are being ticked
            if (body != null) body.setDead();
        }
    }

    // ------------------------------------------------------------------------------------------------------------
    // Per-tick supervision
    // ------------------------------------------------------------------------------------------------------------

    /** Called at the end of every server tick of every player */
    public static void onPlayerTick(EntityPlayerMP player) {
        UUID playerId = player.getUniqueID();
        verifyGameTypeAfterEnd(player);

        DivinationSession session = DivinationRegistry.get(playerId);
        if (session == null || session.dead) return;

        WorldServer world = player.getServerWorld();
        Entity target = world.getEntityFromUuid(session.targetId);
        boolean targetPresent = target instanceof EntityLivingBase && target.isEntityAlive();

        Entity body = world.getEntityFromUuid(session.bodyId);
        boolean homeLoaded = world.isBlockLoaded(new BlockPos(session.x, session.y, session.z));

        boolean usingKit = player.isHandActive() && player.getActiveItemStack().getItem() instanceof ItemTaglockKit;
        DivinationEndReason reason = DivinationRules.evaluateTick(
                player.dimension != session.dimension,
                !player.isSpectator(),
                !targetPresent,
                !usingKit,
                targetPresent && player.getSpectatingEntity() != target,
                DivinationRules.isBodyLost(body != null, body != null && body.isDead, homeLoaded));
        if (reason != null) {
            end(player, reason, null, 0f);
            return;
        }

        // The client ignores the camera packet of entities it does not know yet: send it again once it does
        boolean tracked = world.getEntityTracker().getTrackingPlayers(target).contains(player);
        if (DivinationRules.shouldAttachCamera(session.cameraAttached, true, tracked)) {
            player.connection.sendPacket(new SPacketCamera(target));
            session.cameraAttached = true;
        }
    }

    /**
     * A few ticks after a divination ended, checks that the game mode the server has is the one that was meant,
     * and sends it to the client again. Idempotent, and cheap insurance against server/client game mode desyncs.
     */
    private static void verifyGameTypeAfterEnd(EntityPlayerMP player) {
        DivinationRegistry.PendingResync resync = DivinationRegistry.getPendingResync(player.getUniqueID());
        if (resync == null) return;
        if (--resync.ticksLeft > 0) return;
        DivinationRegistry.clearPendingResync(player.getUniqueID());

        GameType expected = GameType.getByID(resync.gameTypeId);
        if (player.interactionManager.getGameType() != expected) {
            WitcheryCompanion.logger.warn("[Crystal Ball] Game mode of {} drifted after divination ({} instead of {}), fixing",
                    player.getName(), player.interactionManager.getGameType(), expected);
            player.setGameType(expected);
        }
        player.connection.sendPacket(new SPacketChangeGameState(3, (float) expected.getID()));
        player.sendPlayerAbilities();
    }

    // ------------------------------------------------------------------------------------------------------------
    // Death, relog, dimension, orphans
    // ------------------------------------------------------------------------------------------------------------

    /**
     * The player died during a divination. Game mode is NOT restored yet (a Spectator does not drop its inventory,
     * which is the vanilla behaviour for spectators), but the respawned player copies the game mode of the dead one,
     * so it is restored when the player respawns, see {@link #finishAfterDeath}.
     */
    public static void onPlayerDeath(EntityPlayerMP player) {
        DivinationSession session = DivinationRegistry.get(player.getUniqueID());
        if (session == null || session.dead) return;
        session.dead = true;
        removeBody(player.server, session.bodyId);
        notifyClient(player, false);
    }

    /** The player respawned (or logged out) after dying during a divination: put the game mode back */
    public static void finishAfterDeath(EntityPlayerMP player) {
        DivinationSession session = DivinationRegistry.remove(player.getUniqueID());
        if (session == null) return;
        try {
            restoreGameType(player, session);
        } catch (Exception e) {
            WitcheryCompanion.logger.error("[Crystal Ball] Failed to restore game mode of {} after death", player.getName(), e);
        }
        DivinationRecovery.clear(player);
        notifyClient(player, false);
        DivinationRegistry.scheduleResync(player.getUniqueID(), session.originalGameType.getID());
    }

    public static void onPlayerLoggedOut(EntityPlayerMP player) {
        DivinationSession session = DivinationRegistry.get(player.getUniqueID());
        if (session != null) {
            if (session.dead) finishAfterDeath(player);
            else end(player, DivinationEndReason.LOGOUT, null, 0f);
        }
        DivinationRegistry.forgetPlayer(player.getUniqueID());
    }

    public static void onPlayerChangedDimension(EntityPlayerMP player) {
        DivinationSession session = DivinationRegistry.get(player.getUniqueID());
        if (session == null || session.dead) return;
        end(player, DivinationEndReason.DIMENSION_CHANGED, null, 0f);
    }

    /**
     * On login: if a recovery tag is found, the server went down (or the data was saved) in the middle of a divination.
     * The divination is not resumed: game mode and position are restored, and the body is removed if it is loaded
     * (otherwise it is removed as an orphan when its chunk loads).
     */
    public static void onPlayerLoggedIn(EntityPlayerMP player) {
        if (!DivinationRecovery.has(player) || DivinationRegistry.isDiving(player.getUniqueID())) return;
        DivinationRecovery.Data data = DivinationRecovery.read(player);
        DivinationRecovery.clear(player);
        if (data == null) return;

        WitcheryCompanion.logger.warn("[Crystal Ball] {} logged in during an interrupted divination, restoring", player.getName());
        player.setGameType(data.gameType);
        player.capabilities.isFlying = data.flying && player.capabilities.allowFlying;
        player.sendPlayerAbilities();
        if (data.dimension == player.dimension && player.connection != null) {
            player.connection.setPlayerLocation(data.x, data.y, data.z, data.yaw, data.pitch);
            player.server.getPlayerList().serverUpdateMovingPlayer(player);
            player.fallDistance = 0f;
        }
        removeBody(player.server, data.bodyId);
        DivinationRegistry.scheduleResync(player.getUniqueID(), data.gameType.getID());
    }

    /** @return true if a body must not be added to the world, because no divination owns it anymore */
    public static boolean isOrphanBody(Entity entity) {
        return entity instanceof EntitySleepingBody
                && entity.getEntityData().getBoolean(BODY_TAG)
                && !DivinationRegistry.hasBody(entity.getUniqueID());
    }

    // ------------------------------------------------------------------------------------------------------------

    private static void notifyClient(EntityPlayerMP player, boolean active) {
        if (player.connection == null) return;
        CompanionNetworkChannel.NETWORK_CHANNEL.sendTo(new PacketDivinationState.Message(active), player);
    }

}
