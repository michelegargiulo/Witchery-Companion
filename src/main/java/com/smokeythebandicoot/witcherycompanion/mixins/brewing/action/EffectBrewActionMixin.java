package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import kotlin.jvm.internal.Intrinsics;
import net.msrandom.witchery.brewing.EffectLevelCounter;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.EffectBrewAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectBrewAction.class)
public abstract class EffectBrewActionMixin {

    @Shadow(remap = false) @Final
    private int capacityRequired;

    @Shadow @Final private int effectLevel;

    @Inject(method = "augmentEffectLevels", remap = false, cancellable = true, at = @At("HEAD"))
    public void debugEffectLevels(EffectLevelCounter levelCounter, CallbackInfoReturnable<Boolean> cir) {
        boolean result = levelCounter.tryConsumeLevel(this.capacityRequired);
        Utils.logChat("Result: " + result + " - " + "Required: " + this.capacityRequired);
        cir.setReturnValue(result);
    }

    /*
    public void augmentEffectModifiers(ModifiersEffect effectModifiers) {
        // Strength
        if (effectModifiers.strength.getValue() < 7 || effectModifiers.strengthCeilingDisabled) {
            effectModifiers.strength.increase(1);
        }

        // Duration
    }
    */

}
