package com.smokeythebandicoot.witcherypatcher.mixins.common;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.world.World;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 [Bugfix] Fixes PowerSources being lost upon chunk unload/dimension switch. World object changes, and
    for Witchery purposes it is sufficient to check if the dimension has the same ID
 */
@Mixin(value = PowerSources.RelativePowerSource.class, remap = false)
public class RelativePowerSourceMixin {

    @Final
    @Shadow
    private IPowerSource powerSource;

    @Inject(method = "isInWorld", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPisInWorld(World world, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.MixinConfig.MixinBugfixes.BlockFixes.fixAltarPowerSourcePersistency) {
            if (world == null || world.isRemote || this.powerSource.getCurrentWorld() == null)
                cir.setReturnValue(false);
            cir.setReturnValue(world.getWorldType().getId() == this.powerSource.getCurrentWorld().getWorldType().getId());
        }
    }
}
