package com.smokeythebandicoot.witcherycompanion.mixins.witchery.network;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import com.smokeythebandicoot.witcherycompanion.utils.DiviningUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.network.PacketExtendedPlayerSync;
import net.msrandom.witchery.network.WitcheryNetworkPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(PacketExtendedPlayerSync.class)
public abstract class PacketExtendedPlayerSyncMixin implements WitcheryNetworkPacket {

    @Unique
    private DivinationData witchery_Patcher$divinationData;

    @Inject(method = "write", remap = false, at = @At("TAIL"))
    private void writeDivinationData(PacketBuffer buffer, CallbackInfo ci) {
        /*if (witchery_Patcher$divinationData != null) {
            NBTTagCompound divinationDataNBT = new NBTTagCompound();
            DivinationData.writeToNBT(witchery_Patcher$divinationData, divinationDataNBT);
            buffer.writeCompoundTag(divinationDataNBT);
        }*/
    }

    @Inject(method = "apply", remap = false, at = @At("TAIL"))
    private void applyDivinationData(EntityPlayer player, CallbackInfo ci) {
       /* DiviningUtils.setDivinationData(player, this.witchery_Patcher$divinationData);
        if (witchery_Patcher$divinationData != null) {
            DiviningUtils.setDivination(player, witchery_Patcher$divinationData);
        }*/
    }

    @Inject(method = "<init>(Lnet/msrandom/witchery/extensions/PlayerExtendedData;)V", remap = false, at = @At("TAIL"))
    private void injectFirstConstructor(PlayerExtendedData playerEx, CallbackInfo ci) {
        /*IPlayerExtendedDataAccessor accessor = (IPlayerExtendedDataAccessor) playerEx;
        this.witchery_Patcher$divinationData = accessor.getDivinationData();*/
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketBuffer;)V", remap = false, at = @At("TAIL"))
    private void injectSecondConstructor(PacketBuffer buffer, CallbackInfo ci) {
        /*try {
            NBTTagCompound divinationDataNBT = buffer.readCompoundTag();
            this.witchery_Patcher$divinationData = DivinationData.readFromNBT(divinationDataNBT);
        } catch (IOException e) {
            WitcheryCompanion.logger.error("Error while reading Divination Data from PacketBuffer: " + e);
        }*/
    }

}
