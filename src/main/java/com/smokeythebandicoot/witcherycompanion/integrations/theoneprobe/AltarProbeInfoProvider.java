package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;

public class AltarProbeInfoProvider implements IProbeInfoProvider {

    private AltarProbeInfoProvider() { }
    private static AltarProbeInfoProvider INSTANCE = null;
    public static AltarProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AltarProbeInfoProvider();
        }
        return INSTANCE;
    }


    @Override
    public String getID() {
        return WitcheryCompanion.MODID + "_altar";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (!(iBlockState.getBlock() instanceof BlockAltar)) return;
        final TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());
        if (tileEntity instanceof TileEntityAltar) {
            TileEntityAltar altar = (TileEntityAltar) tileEntity;
            if (altar.isValid()) {
                TOPHelper.addText(iProbeInfo, "Power", String.valueOf(altar.getCurrentPower()), TextFormatting.DARK_PURPLE);
            }
        }
    }
}
