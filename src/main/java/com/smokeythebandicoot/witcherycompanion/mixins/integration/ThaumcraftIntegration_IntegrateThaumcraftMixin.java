package com.smokeythebandicoot.witcherycompanion.mixins.integration;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Performance] Reduces startup times by skipping W:R thaumcraft integration if Companion
    Thaumcraft compat is active, as it registers the aspects anyway
 */
@Mixin(targets = "net.msrandom.witchery.integration.ThaumcraftIntegration$IntegrateThaumcraft")
public abstract class ThaumcraftIntegration_IntegrateThaumcraftMixin {

    /** This Mixin avoids running W:R thaumcraft compat during startup is Companion adds its own anyway */
    @Inject(method = "registerAspects", remap = false, cancellable = true, at = @At("HEAD"))
    private static void cancelIfCompanionCompat(CallbackInfo ci) {
        if (ModConfig.IntegrationConfigurations.ThaumcraftIntegration.enableThaumcraftIntegration) {
            ci.cancel();
        }
    }

}
