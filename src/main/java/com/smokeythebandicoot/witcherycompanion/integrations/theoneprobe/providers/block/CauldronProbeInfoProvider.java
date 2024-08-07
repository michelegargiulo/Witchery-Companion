package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.cauldron.ITileEntityCauldronAccessor;
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
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;

public class CauldronProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockWitchCauldron, TileEntityCauldron> {

    private CauldronProbeInfoProvider() { }
    private static CauldronProbeInfoProvider INSTANCE = null;
    public static CauldronProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CauldronProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getProviderName() {
        return "cauldron";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.cauldron;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockWitchCauldron && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityCauldron;
    }

    @Override
    public void addBasicInfo(BlockWitchCauldron block, TileEntityCauldron tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        TOPHelper.addText(iProbeInfo, "Boiling", String.valueOf(tile.isBoiling()), TextFormatting.DARK_PURPLE);
        TOPHelper.addText(iProbeInfo, "Powered", String.valueOf(tile.isPowered()), TextFormatting.DARK_PURPLE);
        if (tile.getActions() != null && tile.getActions().items != null && !tile.getActions().items.isEmpty())
            TOPHelper.itemStacks(iProbeInfo, tile.getActions().items, 10);
    }

    @Override
    public void addExtendedInfo(BlockWitchCauldron block, TileEntityCauldron tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        ITileEntityCauldronAccessor cauldronTileAccessor = (ITileEntityCauldronAccessor) tile;
        float requiredPower = cauldronTileAccessor.accessor_getNeededPower();
        // requiredPower does not update if the Cauldron is not boiling, so simply hide it
        if (requiredPower > 0 && tile.isBoiling())
            TOPHelper.addText(iProbeInfo, "Required Power", String.valueOf(requiredPower), TextFormatting.GOLD);
    }

}
