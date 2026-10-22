package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockGrassper;
import net.msrandom.witchery.block.BlockSilverVat;
import net.msrandom.witchery.block.entity.TileEntityGrassper;
import net.msrandom.witchery.block.entity.TileEntitySilverVat;

import java.util.Collections;

public class SilverVatProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockSilverVat, TileEntitySilverVat> {

    private SilverVatProbeInfoProvider() { }
    private static SilverVatProbeInfoProvider INSTANCE = null;
    public static SilverVatProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SilverVatProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "silver_vat";
    }

    @Override
    public ModConfig.IntegrationConfigurations.TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return ModConfig.IntegrationConfigurations.TopIntegration.silverVat;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockSilverVat && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntitySilverVat;
    }

    @Override
    public void addBasicInfo(BlockSilverVat block, TileEntitySilverVat tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (tile != null) {
            ItemStack itemStack = tile.getStackInSlot(0);
            if (itemStack != null && !itemStack.isEmpty()) {
                TOPHelper.itemStacks(iProbeInfo, Collections.singletonList(itemStack), 1);
            }
        }
    }
}
