package com.smokeythebandicoot.witcherypatcher.mixins.common;

import net.minecraft.world.World;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PowerSources.RelativePowerSource.class, remap = false)
public class RelativePowerSourceMixin {

    @Final
    @Shadow
    private IPowerSource powerSource;

    @Inject(method = "isInWorld", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPisInWorld(World world, CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote) cir.setReturnValue(false);
        cir.setReturnValue(world.getWorldType().getId() == this.powerSource.getCurrentWorld().getWorldType().getId());
    }
}
