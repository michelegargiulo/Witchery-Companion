package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectre.IEntitySpectreAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseEntityProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectre;

public class SpectreProbeInfoProvider extends BaseEntityProbeInfoProvider<EntitySpectre> {

    private SpectreProbeInfoProvider() { }
    private static SpectreProbeInfoProvider INSTANCE = null;
    public static SpectreProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpectreProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "spectre";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.spectre;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData) {
        return entity instanceof EntitySpectre;
    }

    @Override
    public void addBasicInfo(EntitySpectre entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        if (entity instanceof IEntitySpectreAccessor) {
            IEntitySpectreAccessor accessor = (IEntitySpectreAccessor) entity;
            TOPHelper.addText(iProbeInfo, "Lifetime", String.valueOf(accessor.witcherycompanion$accessor$getLifetime()), TextFormatting.RED);
        }
    }

    @Override
    public void addDebugInfo(EntitySpectre entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        if (entity instanceof IEntitySpectreAccessor) {
            IEntitySpectreAccessor accessor = (IEntitySpectreAccessor) entity;
            TOPHelper.addText(iProbeInfo, "Despawn timer", String.valueOf(accessor.witcherycompanion$accessor$getDespawnDelay()), TextFormatting.DARK_RED);
        }
    }
}
