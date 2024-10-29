package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.suncollector.IBlockSunCollectorAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockSunCollector;
import net.msrandom.witchery.block.entity.TileEntityCrystalBall;

public class SunCollectorProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockSunCollector, TileEntity> {

    private SunCollectorProbeInfoProvider() { }
    private static SunCollectorProbeInfoProvider INSTANCE = null;
    public static SunCollectorProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SunCollectorProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "sun_collector";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.sunCollector;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockSunCollector;
    }

    @Override
    public void addBasicInfo(BlockSunCollector block, TileEntity tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (block instanceof IBlockSunCollectorAccessor) {
            IBlockSunCollectorAccessor accessor = (IBlockSunCollectorAccessor) block;
            int power = accessor.getPower(iBlockState);
            TOPHelper.addText(iProbeInfo, "Power", String.valueOf(power), TextFormatting.DARK_PURPLE);
        }
    }
}
