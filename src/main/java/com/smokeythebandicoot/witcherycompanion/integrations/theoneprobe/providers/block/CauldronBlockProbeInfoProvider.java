package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;

public class CauldronBlockProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockWitchCauldron, TileEntityCauldron> {

    private CauldronBlockProbeInfoProvider() { }
    private static CauldronBlockProbeInfoProvider INSTANCE = null;
    public static CauldronBlockProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CauldronBlockProbeInfoProvider();
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
        TOPHelper.itemStacks(iProbeInfo, tile.getActions().items, 2);
    }

    @Override
    public void addExtendedInfo(BlockWitchCauldron block, TileEntityCauldron tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

                Float requiredPower = ReflectionHelper.invokeMethod(block, "getPowerNeeded", null, false);
                if (requiredPower != null && requiredPower > 0)
                    TOPHelper.addText(iProbeInfo, "Required Power", String.valueOf(requiredPower), TextFormatting.GOLD);
    }

}
