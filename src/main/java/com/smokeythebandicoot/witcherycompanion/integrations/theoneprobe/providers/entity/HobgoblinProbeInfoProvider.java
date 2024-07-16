package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity;

import com.smokeythebandicoot.witcherycompanion.api.GoblinTradeApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseEntityProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.entity.EntityGoblin;

public class HobgoblinProbeInfoProvider extends BaseEntityProbeInfoProvider<EntityGoblin> {

    private HobgoblinProbeInfoProvider() { }
    private static HobgoblinProbeInfoProvider INSTANCE = null;
    public static HobgoblinProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HobgoblinProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "goblin";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.goblin;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, Entity entity, IProbeHitEntityData iProbeEntityHitData) {
        return entity instanceof EntityGoblin;
    }

    @Override
    public void addBasicInfo(EntityGoblin entity, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IProbeHitEntityData iProbeHitData) {
        GoblinTradeApi.GoblinProfession profession = GoblinTradeApi.getProfessionByID(entity.getProfession());
        if (profession != null)
            TOPHelper.addText(iProbeInfo, "Profession", profession.professionName, TextFormatting.DARK_AQUA);
    }

}
