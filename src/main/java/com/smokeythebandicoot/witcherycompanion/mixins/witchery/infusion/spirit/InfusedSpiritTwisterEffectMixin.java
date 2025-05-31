package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion.spirit;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritTwisterEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Tweak] Custom delay for Twister Infused Spirit effect
 */
@Mixin(InfusedSpiritTwisterEffect.class)
public abstract class InfusedSpiritTwisterEffectMixin {

    @Inject(method = "getCooldownTicks", remap = false, cancellable = true, at = @At("HEAD"))
    public void tweakCooldown(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ModConfig.PatchesConfiguration.InfusionTweaks.infusedSpiritTwister_tweakCooldown);
    }

}
