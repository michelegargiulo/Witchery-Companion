package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.TreefydApi;
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
import net.msrandom.witchery.entity.EntityTreefyd;

import java.util.ArrayList;
import java.util.List;

public class TreefydProbeInfoProvider extends BaseEntityProbeInfoProvider<EntityTreefyd> {

    private TreefydProbeInfoProvider() { }
    private static TreefydProbeInfoProvider INSTANCE = null;
    public static TreefydProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TreefydProbeInfoProvider();
        }
        return INSTANCE;
    }


    private static PlayerProfileCache playerCache = null;

    @Override
    public String getProviderName() {
        return "treefyd";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.treefyd;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData) {
        return entity instanceof EntityTreefyd;
    }

    @Override
    public void addBasicInfo(EntityTreefyd entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        // Owner
        Entity owner = entity.getOwner();
        String ownerName = owner == null ? "Unknown" : owner.getName();
        TOPHelper.addText(iProbeInfo, "Owner", ownerName, TextFormatting.DARK_AQUA);

        // Is standing?
        boolean isSentinel = entity.isSentinal();
        TOPHelper.addText(iProbeInfo, "Sentinel", String.valueOf(isSentinel), TextFormatting.GREEN);

        if (entity instanceof IEntityTreefydAccessor) {
            // Boost
            IEntityTreefydAccessor accessor = (IEntityTreefydAccessor) entity;
            int boostLevel = accessor.witcherycompanion$accessor$getBoostLevel();
            List<ItemStack> boosts = new ArrayList<>();
            if (boostLevel > 0) {
                boosts.add(new ItemStack(TreefydApi.getLevel1BoostItem()));
            }
            if (boostLevel > 1) {
                boosts.add(new ItemStack(TreefydApi.getLevel2BoostItem()));
            }
            TOPHelper.itemStacks(iProbeInfo, boosts, 10);
        }


    }

}
