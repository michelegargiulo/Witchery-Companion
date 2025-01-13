package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.crystalball.ITileEntityCrystalBallAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
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
import net.msrandom.witchery.block.BlockCrystalBall;
import net.msrandom.witchery.block.entity.TileEntityCrystalBall;

public class CrystalBallProbeInfoProvider extends BaseBlockProbeInfoProvider<BlockCrystalBall, TileEntityCrystalBall> {

    private CrystalBallProbeInfoProvider() { }
    private static CrystalBallProbeInfoProvider INSTANCE = null;
    public static CrystalBallProbeInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalBallProbeInfoProvider();
        }
        return INSTANCE;
    }

    @Override
    public String getProviderName() {
        return "crystal_ball";
    }

    @Override
    public TopIntegration.EProbeElementIntegrationConfig getProbeConfig() {
        return TopIntegration.crystalBall;
    }

    @Override
    public boolean isTarget(EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        return iBlockState.getBlock() instanceof BlockCrystalBall && world.getTileEntity(iProbeHitData.getPos()) instanceof TileEntityCrystalBall;
    }

    @Override
    public void addBasicInfo(BlockCrystalBall block, TileEntityCrystalBall tile, ProbeMode probeMode, IProbeInfo iProbeInfo, EntityPlayer entityPlayer, World world, IBlockState iBlockState, IProbeHitData iProbeHitData) {
        if (tile instanceof ITileEntityCrystalBallAccessor) {
            ITileEntityCrystalBallAccessor accessor = (ITileEntityCrystalBallAccessor) tile;
            long remainingCooldown = (accessor.getLastUsedTime() + BlockTweaks.crystalBall_tweakCooldown) - tile.getWorld().getTotalWorldTime();
            TOPHelper.addText(iProbeInfo, "Cooldown", remainingCooldown <= 0 ? "Ready" : String.valueOf(remainingCooldown), TextFormatting.DARK_PURPLE);
        }
    }
}
