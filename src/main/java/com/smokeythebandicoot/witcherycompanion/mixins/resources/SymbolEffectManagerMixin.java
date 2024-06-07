package com.smokeythebandicoot.witcherycompanion.mixins.resources;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.resources.SymbolEffectManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Tweak] Fixes a log spam
 */
@Mixin(SymbolEffectManager.class)
public abstract class SymbolEffectManagerMixin {

    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
    at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false))
    public void unprintException(Logger instance, String s, Throwable throwable, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.spellEffect_tweakMuteLogSpamOnDisable) {
            WitcheryCompanion.logger.warn(s + ". It might have been disabled in config");
            return;
        }
        original.call(instance, s, throwable);
    }
}
