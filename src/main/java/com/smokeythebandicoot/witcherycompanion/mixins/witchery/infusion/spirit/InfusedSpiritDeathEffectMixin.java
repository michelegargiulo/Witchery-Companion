package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion.spirit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritDeathEffect;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mixin:
 * [Progress] Unlock progress for when players summon death player
 */
@Mixin(InfusedSpiritDeathEffect.class)
public abstract class InfusedSpiritDeathEffectMixin extends InfusedSpiritEffect {

    /** This mixin wraps around the teleportToLocation call. The only info we don't have is entity world, but we have
     * dimension in the params and we only need a remote world obj to retrieve the player list, so we use entity world **/
    @WrapOperation(method = "bindFetish", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/TeleportationUtil;teleportToLocation(Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/entity/Entity;Z)V"))
    private void injectSummonDeathPlayer(BlockPos pos, int dimension, Entity entity, boolean presetPosition, Operation<Void> original) {
        original.call(pos, dimension, entity, presetPosition);
        List<EntityPlayerMP> players = witcherycompanion$getNearbyPlayers(entity.world, pos, dimension);
        for (EntityPlayerMP player : players) {
            ProgressUtils.unlockProgress(player, WitcheryCompanion.prefix("creatures/death_player_summon"),
                    WitcheryProgressEvent.EProgressTriggerActivity.BIND_TO_FETISH.activityTrigger);
        }
    }

    @Unique
    private List<EntityPlayerMP> witcherycompanion$getNearbyPlayers(World world, BlockPos pos, int dimension) {
        // Retrieve player list
        if (world.isRemote) {
            return Collections.emptyList();
        }
        MinecraftServer server = world.getMinecraftServer();
        if (server == null) {
            return Collections.emptyList();
        }

        // Filter and return players
        return world.getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.dimension == dimension &&
                        player.getDistanceSq(pos) < 100.0)
                .collect(Collectors.toList());
    }

}
