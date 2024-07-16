package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseEntityProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
        return TopIntegration.goblin;
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
    public void addExtendedInfo(EntityImp entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Cooldown
        Long lastGiftTime = ReflectionHelper.getField(entity, "lastGiftTime", false);
        if (lastGiftTime != null && entity.isTamed()) {
            long curTime = MinecraftServer.getCurrentTimeMillis() / 50L;
            long cooldown = (lastGiftTime + EntityTweaks.flameImp_tweakGiftDelayTicks) - curTime;
            TOPHelper.addText(iProbeInfo, "Cooldown", curTime >= 0 ? "Ready" : String.valueOf(cooldown), TextFormatting.RED);
        }
    }

    @Override
    public void addDebugInfo(EntityImp entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Level
        Integer level = ReflectionHelper.getField(entity, "secretsShared", false);
        if (level != null) {
            TOPHelper.addText(iProbeInfo, "Trade Level", level.toString(), TextFormatting.GOLD);
        }
    }



}
