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
import net.msrandom.witchery.block.entity.TileEntityGrassper;

import java.util.Collections;

public class GrassperProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockGrassper, TileEntityGrassper> {

    private GrassperProbeInfoProvider() { }
    private static GrassperProbeInfoProvider INSTANCE = null;
    public static GrassperProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GrassperProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "grassper";
    }

    @Override
    public ModConfig.IntegrationConfigurations.TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return ModConfig.IntegrationConfigurations.TopIntegration.grassper;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockGrassper && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityGrassper;
    }

    @Override
    public void addBasicInfo(BlockGrassper block, TileEntityGrassper tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (tile != null) {
            ItemStack itemStack = tile.getStackInSlot(0);
            if (itemStack != null && !itemStack.isEmpty()) {
                IProbeInfo horizontal = iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle()
                        .alignment(ElementAlignment.ALIGN_CENTER).spacing(2));
                TOPHelper.itemStacks(horizontal, Collections.singletonList(itemStack), 1);
                TOPHelper.addText(horizontal, "Held item", itemStack.getDisplayName(), TextFormatting.WHITE);
            }
        }
    }
}
