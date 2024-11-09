package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.accessors.crystalball.ITileEntityCrystalBallAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.tileentity.TileEntity;
import net.msrandom.witchery.block.entity.TileEntityCrystalBall;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Configurable cooldown
 */
@Mixin(TileEntityCrystalBall.class)
public abstract class TileEntityCrystalBallMixin extends TileEntity implements ITileEntityCrystalBallAccessor {

    @Shadow(remap = false)
    private long lastUsedTime;

    /** This Mixin changes the hardcoded constant of 500 Altar power for a Crystal Ball prediction **/
    @ModifyConstant(method = "canBeUsed", remap = false, constant = @Constant(longValue = 100L))
    private long modifyRequiredPower(long constant) {
        return ModConfig.PatchesConfiguration.BlockTweaks.crystalBall_tweakCooldown;
    }

    @Override
    public long getLastUsedTime() {
        return this.lastUsedTime;
    }
}
