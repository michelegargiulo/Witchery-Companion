package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class BaseEntityProbeInfoProvider<E extends Entity>  implements IProbeInfoEntityProvider {

    // ------ BASE METHODS ------ //
    public abstract String getProviderName();

    // ------ HELPERS ------ //
    public abstract TopIntegration.EProbeElementIntegrationConfig getProbeConfig();

    public abstract boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData);

    public void addBasicInfo(E entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) { };

    public void addExtendedInfo(E entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) { };

    public void addDebugInfo(E entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) { };

    protected final boolean shouldAddBasic(ProbeMode probeMode) {
        return getProbeConfig() != TopIntegration.EProbeElementIntegrationConfig.DISABLED;
    }

    protected final boolean shouldAddExtended(ProbeMode probeMode) {
        TopIntegration.EProbeElementIntegrationConfig cfg = getProbeConfig();

        if (cfg == TopIntegration.EProbeElementIntegrationConfig.DISABLED || cfg == TopIntegration.EProbeElementIntegrationConfig.BASIC_ONLY) return false;
        if (cfg == TopIntegration.EProbeElementIntegrationConfig.ALL_BASIC) return true;
        return cfg == TopIntegration.EProbeElementIntegrationConfig.DEFAULT && probeMode == ProbeMode.EXTENDED;

    }

    protected final boolean shouldAddDebug(ProbeMode probeMode) {
        return probeMode == ProbeMode.DEBUG;
    }

    // ------ OVERRIDES ------ //
    @Override
    public final String getID() {
        return WitcheryCompanion.MODID + ":" + getProviderName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {

        if (!isTarget(entityPlayer, world, entity, iProbeHitEntityData)) return;

        // This Cast will fail if Child classes fail to implement properly the "isTarget" function
        E targetEntity = (E)entity;

        if (shouldAddBasic(probeMode))
            addBasicInfo(targetEntity, probeMode, iProbeInfo, entityPlayer, world, iProbeHitEntityData);

        if (shouldAddExtended(probeMode))
            addExtendedInfo(targetEntity, probeMode, iProbeInfo, entityPlayer, world, iProbeHitEntityData);

        if (shouldAddDebug(probeMode))
            addDebugInfo(targetEntity, probeMode, iProbeInfo, entityPlayer, world, iProbeHitEntityData);
    }
}
