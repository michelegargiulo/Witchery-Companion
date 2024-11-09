package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.kettle.ITileEntityKettleAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.BaseBlockProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPHelper;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.msrandom.witchery.block.BlockKettle;
import net.msrandom.witchery.block.entity.TileEntityKettle;

import java.util.List;

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
        TOPHelper.addText(iProbeInfo, "Ruined", String.valueOf(accessor.getIsRuined()), TextFormatting.DARK_PURPLE);

        String requiredFamiliar = accessor.requiredFamiliar();
        if (requiredFamiliar != null) {
            boolean satisfied = accessor.satisfyFamiliar(entityPlayer);
            String prettyFamName = I18n.format("witcherycompanion.probe.familiar_power." + requiredFamiliar + ".name");
            TOPHelper.addText(iProbeInfo, "Familiar Power", prettyFamName, satisfied ? TextFormatting.GREEN : TextFormatting.RED);
        }

        Integer requiredDimension = accessor.requiredDimension();
        if (requiredDimension != null) {
            boolean satisfied = entityPlayer.world.provider.getDimension() == requiredDimension;
            String prettyDimName = I18n.format("witcherycompanion.probe.dimension." + DimensionManager.getProvider(requiredDimension).getDimensionType().getName() + ".name");
            TOPHelper.addText(iProbeInfo, "Dimension", prettyDimName, satisfied ? TextFormatting.GREEN : TextFormatting.RED);
        }

        // Divide the items list in inputs (0-5) and outputs (6)
        NonNullList<ItemStack> items = accessor.getItems();
        List<ItemStack> inputs = items.subList(0, 6);
        List<ItemStack> output = items.subList(6, 7);

        // Check if there's at least one ingredient. Ingredients
        // are ordered, so if the first is air, all of them are
        if (!inputs.get(0).isEmpty())
            TOPHelper.itemStacks(iProbeInfo, inputs, 10);

        // If there's output, display the output
        if (!output.get(0).isEmpty()) {
            IProbeInfo horizontal = iProbeInfo.horizontal(iProbeInfo.defaultLayoutStyle()
                    .alignment(ElementAlignment.ALIGN_CENTER));
            horizontal.text("§6Result: ");
            TOPHelper.itemStacks(horizontal, output, 1);
            horizontal.text(output.get(0).getDisplayName());
        }
    }

    @Override
    public void addExtendedInfo(BlockKettle block, TileEntityKettle tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        ITileEntityKettleAccessor accessor = (ITileEntityKettleAccessor) tile;
        float requiredPower = accessor.getCurrentPowerNeeded();
        // requiredPower does not update if the Cauldron is not boiling, so simply hide it
        // Different from the cauldron, there are recipes which require no power. So show power 0 as requirement
        if (!accessor.getIsRuined() && accessor.getCurrentPowerNeeded() > -1.0f)
            TOPHelper.addText(iProbeInfo, "Required Power", String.valueOf(requiredPower), TextFormatting.GOLD);
    }

}
