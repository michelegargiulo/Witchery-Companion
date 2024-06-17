package com.smokeythebandicoot.witcherycompanion.mixins.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.msrandom.witchery.potion.PotionInsanity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix illusion entities being idle and non-intractable due to being spawned on the clientside
 */
@Mixin(PotionInsanity.class)
public class PotionInsanityMixin {

    @Inject(method = "performEffect", remap = true, cancellable = true, at = @At("HEAD"))
    public void fixGhostEntities(EntityLivingBase entity, int amplifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.PotionTweaks.insanity_fixGhostEntities && entity.world.isRemote) {
            ci.cancel();
        }
    }

}
