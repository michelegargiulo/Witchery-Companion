package com.smokeythebandicoot.witcherycompanion.mixins.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.potion.PotionResizing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixin:
 [Tweak] Set Custom sizes for the potion effect (Also allows to fix a glitch where the player eyes can x-ray through
    blocks at the smallest size
 */
@Mixin(PotionResizing.class)
public class PotionResizingMixin {

    @Inject(method = "getScaleFactor", remap = false, cancellable = true, at = @At("HEAD"))
    private static void modifyResizingRates(int amplifier, CallbackInfoReturnable<Float> cir) {
        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_tweakCustomSizes) return;
        float customValue = 1.0f;
        switch (amplifier) {
            case 0:
                customValue = ModConfig.PatchesConfiguration.PotionTweaks.resizing_tweakCustomSizeSmallest;
                break;
            case 1:
                customValue = ModConfig.PatchesConfiguration.PotionTweaks.resizing_tweakCustomSizeSmaller;
                break;
            case 2:
                customValue = ModConfig.PatchesConfiguration.PotionTweaks.resizing_tweakCustomSizeBigger;
                break;
            case 3:
                customValue = ModConfig.PatchesConfiguration.PotionTweaks.resizing_tweakCustomSizeBiggest;
                break;
        }
        cir.setReturnValue(customValue);

    }

}
