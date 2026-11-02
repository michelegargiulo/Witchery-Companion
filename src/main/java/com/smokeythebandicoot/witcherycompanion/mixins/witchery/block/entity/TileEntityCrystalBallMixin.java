package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.crystalball.ITileEntityCrystalBallAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.msrandom.witchery.block.entity.TileEntityCrystalBall;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Configurable cooldown
 * [Bugfix] Sync the last used time to the clients, so that the cooldown is correct on the client side too
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

    /** W:R sets lastUsedTime in onUsed() but never notifies the clients, so their copy of the tile keeps the old value
     * (0 for a Crystal Ball that was never loaded after being used) and anything that reads the cooldown client-side
     * sees a Crystal Ball that is always ready. W:R already sends lastUsedTime in getUpdatePacket(), so only the update
     * needs to be requested. markDirty() also makes sure the new value is saved with the chunk */
    @Inject(method = "onUsed", remap = false, at = @At("RETURN"))
    private void witcherycompanion$syncLastUsedTime(CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.BlockTweaks.crystalBall_fixCooldownSync) return;
        if (this.world == null || this.world.isRemote) return;

        this.markDirty();
        IBlockState state = this.world.getBlockState(this.getPos());
        this.world.notifyBlockUpdate(this.getPos(), state, state, 3);
    }

    @Override
    public long witcherycompanion$accessor$getLastUsedTime() {
        return this.lastUsedTime;
    }
}
