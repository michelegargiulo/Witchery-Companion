package com.smokeythebandicoot.witcherycompanion.mixins.witchery.world.dimension.torment;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.DimensionTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.world.dimension.torment.TormentWorldData;
import net.msrandom.witchery.world.dimension.torment.WorldProviderTorment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Allows disabling every teleportation to the Torment Dimension, whatever its source (Lord of Torment, Torment
    Portal, Tormentum symbol). Witchery never teleports players from those sources directly: it queues a pending
    torment on the player (setPlayerMustTorment) and updatePlayerEffects performs the teleportation. Cancelling the
    queueing here stops all of them at once, without leaving a stale pending teleportation behind
 */
@Mixin(WorldProviderTorment.class)
public class WorldProviderTormentMixin {

    /** Only "beginning" types (BEGIN, BOSS) are blocked. END is how the Torment Portal sends players back to the
     * Overworld, and it must always work, or players already inside the dimension would be trapped there */
    @Inject(method = "setPlayerMustTorment", remap = false, cancellable = true, at = @At("HEAD"))
    private static void witcherycompanion$blockTeleportationToTorment(EntityPlayer player, TormentWorldData.TormentType type, int level, CallbackInfo ci) {
        if (DimensionTweaks.torment_tweakDisableTeleportation && type.isBeginning()) {
            ci.cancel();
        }
    }

}
