package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.infernalimp.IEntityImpAccessor;
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
import net.msrandom.witchery.entity.EntityImp;

public class ImpProbeInfoProvider extends BaseEntityProbeInfoProvider<EntityImp> {

    private ImpProbeInfoProvider() { }
    private static ImpProbeInfoProvider INSTANCE = null;
    public static ImpProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImpProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "imp";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.imp;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData) {
        return entity instanceof EntityImp;
    }

    @Override
    public void addBasicInfo(EntityImp entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Has contract ?
        Entity owner = entity.getOwner();
        String ownerName = owner == null ? "Unknown" : owner.getName();
        TOPHelper.addText(iProbeInfo, "Has Contract", entity.isTamed() ? "with " + ownerName : "No", TextFormatting.DARK_AQUA);
    }

    @Override
    public void addDebugInfo(EntityImp entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Cast to Accessor to avoid reflection
        IEntityImpAccessor impAccessor = (IEntityImpAccessor)(Object)entity;

        // Cooldown
        if (entity.isTamed()) {
            long cooldown = impAccessor.accessor_getCooldown();
            int seconds = (int)Math.ceil((double)cooldown / 20); // Cooldown in Seconds
            TOPHelper.addText(iProbeInfo, "Cooldown", seconds <= 0 ? "Ready" : seconds + " s", TextFormatting.RED);

        }

        // Level
        int level = impAccessor.accessor_getSecretsShared();
        TOPHelper.addText(iProbeInfo, "Trade Level", String.valueOf(level), TextFormatting.GOLD);

    }



}
