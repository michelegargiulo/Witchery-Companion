package com.smokeythebandicoot.witcherypatcher.mixins.block.entity;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fixes PowerSources being lost upon world load/reload, requiring them to be interacted or
    broken and re-placed to be registered correctly again
 */
@Mixin(value = TileEntityAltar.class, remap = false)
public abstract class TileEntityAltarMixin extends WitcheryTileEntity implements IPowerSource {

    @Shadow
    private float power;

    @Inject(method = "readFromNBT", at = @At("HEAD"), remap = false)
    private void WPreadFromNBT(NBTTagCompound nbtTag, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.altar_fixPowerSourcePersistency)
            if (nbtTag.hasKey("Core")) {
                PowerSources.instance().registerPowerSource(this);
            }
    }

    @Inject(method = "consumePower", at = @At("HEAD"), remap = false, cancellable = true)
    public void WPconsumePower(float power, CallbackInfoReturnable<Boolean> cir) {
        if (this.power >= power) {
            this.power -= power;
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

}
