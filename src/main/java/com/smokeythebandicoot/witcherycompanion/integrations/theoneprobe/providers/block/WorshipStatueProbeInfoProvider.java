package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.suncollector.IBlockSunCollectorAccessor;
import com.smokeythebandicoot.witcherycompanion.api.worshipstatue.ITileEntityWorshipStatueAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockStatueOfWorship;
import net.msrandom.witchery.block.BlockSunCollector;
import net.msrandom.witchery.block.entity.TileEntityWorshipStatue;

public class WorshipStatueProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockStatueOfWorship, TileEntityWorshipStatue> {

    private WorshipStatueProbeInfoProvider() { }
    private static WorshipStatueProbeInfoProvider INSTANCE = null;
    public static WorshipStatueProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WorshipStatueProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "worship_statue";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.statueOfHobgoblinPatron;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockStatueOfWorship && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityWorshipStatue;
    }

    @Override
    public void addBasicInfo(BlockStatueOfWorship block, TileEntityWorshipStatue tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (tile instanceof ITileEntityWorshipStatueAccessor) {
            ITileEntityWorshipStatueAccessor accessor = (ITileEntityWorshipStatueAccessor) tile;
            TOPHelper.addText(iProbeInfo, "Worship Level", String.valueOf(accessor.getWorshipLevel()), TextFormatting.DARK_PURPLE);
        }
    }
}
