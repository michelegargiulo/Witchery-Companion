package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.network;

import com.smokeythebandicoot.witcherycompanion.patches.crystalball.DivinationRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketSpectate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Spectator Divination: a diviner cannot use the Spectator menu to teleport to other entities or dimensions
 */
@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {

    @Shadow
    public EntityPlayerMP player;

    /** No-op when the player is not diving. Runs before the packet is re-queued: the registry is thread-safe */
    @Inject(method = "handleSpectate", at = @At("HEAD"), cancellable = true)
    private void witcherycompanion$blockSpectateTeleportWhileDiving(CPacketSpectate packet, CallbackInfo ci) {
        if (DivinationRegistry.isDiving(this.player.getUniqueID())) ci.cancel();
    }

}
