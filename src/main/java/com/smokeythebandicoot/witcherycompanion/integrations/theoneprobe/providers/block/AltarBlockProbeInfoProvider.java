package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;

public class AltarBlockProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockAltar, TileEntityAltar> {

    private AltarBlockProbeInfoProvider() { }
    private static AltarBlockProbeInfoProvider INSTANCE = null;
    public static AltarBlockProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AltarBlockProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "altar";
    }

    @Override
    public ModConfig.IntegrationConfigurations.TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return ModConfig.IntegrationConfigurations.TopIntegration.altar;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockAltar && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityAltar;
    }

    @Override
    public void addBasicInfo(BlockAltar block, TileEntityAltar tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        // If TileEntityAltar does not exist, try to find the core
        if (tile == null || !tile.isValid()) {
            BlockAltar blockAltar = (BlockAltar)iBlockState.getBlock();
            tile = findCoreAltar(world, iProbeHitData.getPos(), block);
            // If still no Core Altar is found, return
            if (tile == null) return;
        }

        // Add Altar information
        TOPHelper.addText(iProbeInfo, "Power", String.valueOf(tile.getCurrentPower()), TextFormatting.DARK_PURPLE);
        TOPHelper.addText(iProbeInfo, "Max Power", String.valueOf(tile.getMaxPower()), TextFormatting.DARK_PURPLE);
        TOPHelper.addText(iProbeInfo, "Recharge Rate", String.valueOf(tile.getRechargeScale()), TextFormatting.DARK_PURPLE);
    }

    @Override
    public void addDebugInfo(BlockAltar block, TileEntityAltar tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        TOPHelper.addText(iProbeInfo, "Is Core: ", String.valueOf(tile.isValid()), TextFormatting.RED);
        if (!tile.isValid()) {
            TileEntityAltar coreAltar = findCoreAltar(world, iProbeHitData.getPos(), block);
            if (coreAltar == null) return;
            TOPHelper.addText(iProbeInfo, "Core Pos: ", String.valueOf(coreAltar.getLocation()), TextFormatting.GOLD);
        }
    }

    private TileEntityAltar findCoreAltar(World world, BlockPos pos, BlockAltar altarBlock) {
        BlockPos corePos = ReflectionHelper.invokeMethod(altarBlock, "getCore",
                new Class<?>[]{IBlockAccess.class, BlockPos.class}, false, world, pos);
        if (corePos == null) return null;
        TileEntity te = world.getTileEntity(corePos);
        if (!(te instanceof TileEntityAltar)) return null;
        return (TileEntityAltar) te;
    }
}
