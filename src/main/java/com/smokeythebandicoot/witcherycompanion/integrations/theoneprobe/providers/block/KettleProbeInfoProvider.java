package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.cauldron.ITileEntityCauldronAccessor;
import com.smokeythebandicoot.witcherycompanion.api.kettle.ITileEntityKettleAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockKettle;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.block.entity.TileEntityKettle;

public class KettleProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockKettle, TileEntityKettle> {

    private KettleProbeInfoProvider() { }
    private static KettleProbeInfoProvider INSTANCE = null;
    public static KettleProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KettleProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "kettle";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.kettle;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockKettle && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityKettle;
    }

    @Override
    public void addBasicInfo(BlockKettle block, TileEntityKettle tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        ITileEntityKettleAccessor accessor = (ITileEntityKettleAccessor)tile;
        TOPHelper.addText(iProbeInfo, "Powered", String.valueOf(tile.isPowered), TextFormatting.DARK_PURPLE);
        TOPHelper.addText(iProbeInfo, "Ruined", String.valueOf(accessor.accessor_getIsRuined()), TextFormatting.DARK_PURPLE);
        TOPHelper.itemStacks(iProbeInfo, accessor.accessor_getItems(), 10);
    }

    @Override
    public void addExtendedInfo(BlockKettle block, TileEntityKettle tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        ITileEntityKettleAccessor accessor = (ITileEntityKettleAccessor) tile;
        float requiredPower = accessor.accessor_getCurrentPowerNeeded();
        // requiredPower does not update if the Cauldron is not boiling, so simply hide it
        if (requiredPower > 0 && !accessor.accessor_getIsRuined())
            TOPHelper.addText(iProbeInfo, "Required Power", String.valueOf(requiredPower), TextFormatting.GOLD);
    }

}
