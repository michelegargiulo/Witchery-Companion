package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.PotionBrewEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewEffect.Companion.class)
public abstract class PotionBrewEffectCompanionMixin {

    /* should be removeable
    @Inject(method = "applyPotionEffect(Lnet/minecraft/entity/EntityLivingBase;Lnet/msrandom/witchery/brewing/ModifiersEffect;Lnet/minecraft/potion/Potion;IZLnet/minecraft/entity/player/EntityPlayer;I)V",
            remap = false, cancellable = true, at = @At("HEAD"))
    public void debugApplyPotionEffect(EntityLivingBase entity, ModifiersEffect modifiers, Potion potion, int duration, boolean noParticles, EntityPlayer thrower, int strengthCeiling, CallbackInfo ci) {
        //int strength = RangesKt.coerceAtMost(modifiers.getStrength(), modifiers.strengthCeilingDisabled ? 10 : strengthCeiling);
        //int strength = ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakGiftDelayTicks;
        if (potion.isInstant()) {
            potion.affectEntity(null, thrower, entity, strength, modifiers.powerScalingFactor);
        } else {
            entity.addPotionEffect(new PotionEffect(potion, modifiers.getModifiedDuration(duration), strength, noParticles, true));
        }
        ci.cancel();
    }
    */

}
