package com.smokeythebandicoot.witcherypatcher.mixins.block.entity;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.common.IPowerSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityAltar.class, remap = false)
public abstract class TileEntityAltarMixin extends WitcheryTileEntity {

    @Shadow
    private float power;
    @Shadow
    private float maxPower;
    @Shadow
    private int powerScale;
    @Shadow
    private int rechargeScale;
    @Shadow
    private int rangeScale;
    @Shadow
    private int enhancementLevel;

    @Shadow
    public abstract void updatePower();

    /*
    @Inject(method = "readFromNBT", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPreadFromNBT(NBTTagCompound nbtTag, CallbackInfo ci) {
        super.readFromNBT(nbtTag);
        if (nbtTag.hasKey("Core")) {
            this.maxPower = nbtTag.getFloat("MaxPower");
            this.powerScale = nbtTag.getInteger("PowerScale");
            this.rechargeScale = nbtTag.getInteger("RechargeScale");
            this.rangeScale = nbtTag.getInteger("RangeScale");
            this.enhancementLevel = nbtTag.getInteger("EnhancementLevel");
            this.updatePower();
        }
        ci.cancel();
        //super.readFromNBT(nbtTag);
    }
    */

    @Inject(method = "consumePower", at = @At("HEAD"), remap = false)
    private void WPconsumePower(float power, CallbackInfoReturnable<Boolean> cir) {
        updatePower();
    }
}
