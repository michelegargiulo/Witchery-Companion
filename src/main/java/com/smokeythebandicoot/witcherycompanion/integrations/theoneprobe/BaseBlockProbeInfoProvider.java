package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BaseBlockProbeInfoProvider<B extends Block, T extends TileEntity>  implements IProbeInfoProvider {

    // ------ BASE METHODS ------ //
    public abstract String getProviderName();

    // ------ HELPERS ------ //
    public abstract TopIntegration.EProbeElementIntegrationConfig getProbeConfig();

    public abstract boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData);

    public void addBasicInfo(B block, T tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) { }

    public void addExtendedInfo(B block, T tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) { }

    public void addDebugInfo(B block, T tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) { }

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
        return WitcheryCompanion.prefix(getProviderName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        if (!isTarget(entityPlayer, world, iBlockState, iProbeHitData)) return;

        // This Cast will fail if Child classes fail to implement properly the "isTarget" function
        B block = (B)iBlockState.getBlock();
        T tile = (T)world.getTileEntity(iProbeHitData.getPos());

        if (shouldAddBasic(probeMode))
            addBasicInfo(block, tile, probeMode, iProbeInfo, entityPlayer, world, iBlockState, iProbeHitData);

        if (shouldAddExtended(probeMode))
            addExtendedInfo(block, tile, probeMode, iProbeInfo, entityPlayer, world, iBlockState, iProbeHitData);

        if (shouldAddDebug(probeMode))
            addDebugInfo(block, tile, probeMode, iProbeInfo, entityPlayer, world, iBlockState, iProbeHitData);
    }
}
