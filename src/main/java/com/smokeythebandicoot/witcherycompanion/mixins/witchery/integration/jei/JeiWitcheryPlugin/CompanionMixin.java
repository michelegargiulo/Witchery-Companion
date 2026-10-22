package com.smokeythebandicoot.witcherycompanion.mixins.witchery.integration.jei.JeiWitcheryPlugin;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.integration.jei.JeiWitcheryPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Bugfix] Fix JEI force reloading on world load or dimension change
 */
@Mixin(JeiWitcheryPlugin.Companion.class)
public class CompanionMixin {

    /**
     * Here we prevent JEI reloading since it's no longer needed.
     */
    @Inject(method = "reload", at = @At("HEAD"), cancellable = true, remap = false)
    private void reload(CallbackInfo ci) {
        if(ModConfig.IntegrationConfigurations.JeiIntegration.fixJeiForceReloading) {
            ci.cancel();
        }
    }

}
