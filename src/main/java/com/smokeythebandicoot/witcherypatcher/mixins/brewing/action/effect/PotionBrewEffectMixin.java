package com.smokeythebandicoot.witcherypatcher.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.PotionBrewEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Disable Strength Ceiling. Kinda also a bugfix, as disabling the ceiling will fix quite some bugs regarding potion scaling not working
 */
@Mixin(value = PotionBrewEffect.class)
public class PotionBrewEffectMixin {


    @Shadow(remap = false) @Final
    private int strengthCeiling;

    @Inject(method = "applyPotionEffect(Lnet/minecraft/entity/EntityLivingBase;Lnet/msrandom/witchery/brewing/ModifiersEffect;Lnet/minecraft/potion/Potion;IZLnet/minecraft/entity/player/EntityPlayer;I)V",
        remap = false, at = @At("HEAD"), cancellable = true)
    private static void WPdisableStrengthCeiling(EntityLivingBase entity, ModifiersEffect modifiers, Potion potion, int duration, boolean noParticles, EntityPlayer thrower, int strengthCeiling, CallbackInfo ci) {

        if (ModConfig.PatchesConfiguration.BrewsTweaks.common_tweakDisableStrengthCeiling) { // Strength must be between 0 and 10
            int strength = Math.min(modifiers.getStrength(), 10);
            if (strength < 0) strength = 0;

            if (potion.isInstant()) {
                potion.affectEntity(null, thrower, entity, strength, modifiers.powerScalingFactor);
            } else {
                entity.addPotionEffect(new PotionEffect(potion, modifiers.getModifiedDuration(duration), strength, noParticles, true));
            }
        }
    }

}
