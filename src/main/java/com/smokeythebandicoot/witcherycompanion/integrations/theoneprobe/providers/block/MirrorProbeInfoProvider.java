package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.crystalball.ITileEntityCrystalBallAccessor;
import com.smokeythebandicoot.witcherycompanion.api.mirror.IBlockMirrorAccessor;
import com.smokeythebandicoot.witcherycompanion.api.mirror.ITileEntityMirrorAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
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
import net.msrandom.witchery.block.BlockMirror;
import net.msrandom.witchery.block.entity.TileEntityMirror;

public class MirrorProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockMirror, TileEntityMirror> {

    private MirrorProbeInfoProvider() { }
    private static MirrorProbeInfoProvider INSTANCE = null;
    public static MirrorProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MirrorProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "mirror";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.crystalBall;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockMirror && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityMirror;
    }

    @Override
    public void addBasicInfo(BlockMirror block, TileEntityMirror tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {

        if (block instanceof IBlockMirrorAccessor) {
            IBlockMirrorAccessor blockAccessor = (IBlockMirrorAccessor) block;
            if (blockAccessor.isExit()) {
                iProbeInfo.text(TextFormatting.GREEN + "Exit");
            }
        }

        /* Mirror is a bit buggy and info is not accurate. Since the Mirror functionally works, a re-coding will be done
         * with low priority. For now only isExit will be displayed

        TileEntity te = tile;
        if (iBlockState.getValue(BlockMirror.TOP)) {
            iBlockState = world.getBlockState(iProbeHitData.getPos().down());
            te = world.getTileEntity(iProbeHitData.getPos().down());
        }

        if (te instanceof ITileEntityMirrorAccessor && iBlockState.getBlock() instanceof IBlockMirrorAccessor) {
            IBlockMirrorAccessor blockAccessor = (IBlockMirrorAccessor) iBlockState.getBlock();
            ITileEntityMirrorAccessor accessor = (ITileEntityMirrorAccessor) te;
            long remainingCooldown = accessor.getCooldown();
            TOPHelper.addText(iProbeInfo, "Hollow", String.valueOf(accessor.isHollow()), TextFormatting.DARK_PURPLE);
            TOPHelper.addText(iProbeInfo, "Cooldown", remainingCooldown <= 0 ? "Ready" : String.valueOf(remainingCooldown), TextFormatting.DARK_PURPLE);

            if (blockAccessor.isExit()) {
                iProbeInfo.text(TextFormatting.GREEN + "Exit");
            }
        }*/
    }
}
