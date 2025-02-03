package com.smokeythebandicoot.witcherycompanion.mixins.witchery.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.potion.PotionMortalCoil;
import net.msrandom.witchery.potion.WitcheryPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionMortalCoil.class)
public abstract class PotionMortalCoilMixin extends WitcheryPotion {

    private PotionMortalCoilMixin(int color) {
        super(color);
    }

    @Inject(method = "performEffect", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakDontKillSpectators(EntityLivingBase entity, int amplifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.PotionTweaks.mortalCoil_tweakSpareSpectators && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isSpectator()) {
                ci.cancel();
            }
        }
    }
}
