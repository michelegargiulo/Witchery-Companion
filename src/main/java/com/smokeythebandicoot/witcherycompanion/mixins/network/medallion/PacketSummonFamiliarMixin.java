package com.smokeythebandicoot.witcherycompanion.mixins.network.medallion;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.network.medallion.PacketFamiliarCheat;
import net.msrandom.witchery.network.medallion.PacketSummonFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketSummonFamiliar.class)
public abstract class PacketSummonFamiliarMixin extends PacketFamiliarCheat {

    @Shadow(remap = false)
    private boolean summon;

    private PacketSummonFamiliarMixin(EnumHand hand, String name) {
        super(hand, name);
    }

    @Inject(method = "affectPlayer", remap = false, cancellable = true, at = @At("HEAD"))
    public void injectPacket(EntityPlayer player, CallbackInfo ci) {
        Utils.logChat("Received Summon Familiar Packet. World: " + (player.world.isRemote ? "REMOTE" : "LOCAL"));
        Familiar<?> familiar;
        String packetName;
        Entity entity;
        if (this.summon) {
            familiar = Familiars.summonFamiliar(player, player.posX, player.posY, player.posZ);
            if (familiar != null) {
                packetName = this.getName();
                if (packetName == null) {
                    packetName = familiar.getEntity().getCustomNameTag();
                }
                entity = familiar.getEntity();
                entity.setCustomNameTag(packetName);
            }
        } else {
            familiar = Familiars.getBoundFamiliar(player);
            if (familiar != null) {
                familiar.dismiss(player);
                entity = familiar.getEntity();
                packetName = this.getName();
                if (packetName == null) {
                    packetName = familiar.getEntity().getCustomNameTag();
                }

                entity.setCustomNameTag(packetName);
            }
        }
        ci.cancel();
    }

}
