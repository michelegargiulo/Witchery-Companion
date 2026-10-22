package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.TreefydApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectralfamiliar.IEntitySpectralFamiliarAccessor;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.treefyd.IEntityTreefydAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseEntityProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectralFamiliarProbeInfoProvider extends BaseEntityProbeInfoProvider<EntitySpectralFamiliar> {

    private SpectralFamiliarProbeInfoProvider() { }
    private static SpectralFamiliarProbeInfoProvider INSTANCE = null;
    public static SpectralFamiliarProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpectralFamiliarProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "spectral_familiar";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.spectralFamiliar;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData) {
        return entity instanceof EntitySpectralFamiliar;
    }

    @Override
    public void addBasicInfo(EntitySpectralFamiliar entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Owner
        Entity owner = entity.getOwner();
        String ownerName = owner == null ? "Unknown" : owner.getName();
        TOPHelper.addText(iProbeInfo, "Owner", ownerName, TextFormatting.DARK_AQUA);

        // Sniffed item
        if (entity instanceof IEntitySpectralFamiliarAccessor) {
            IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) entity;
            ItemStack sniff = accessor.witcherycompanion$accessor$getSniffedItem();
            if (!sniff.isEmpty()) {
                IProbeInfo line = iProbeInfo.horizontal();
                TOPHelper.addText(line, "Sniffed", "", TextFormatting.AQUA);
                TOPHelper.itemStacks(line, Collections.singletonList(sniff), 1);
            }
        }

    }

    @Override
    public void addDebugInfo(EntitySpectralFamiliar entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Search Count
        if (entity instanceof IEntitySpectralFamiliarAccessor) {
            IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) entity;
            TOPHelper.addText(iProbeInfo, "Searches", String.valueOf(accessor.witcherycompanion$accessor$getCurrentSearches()), TextFormatting.GREEN);
        }
    }
}
