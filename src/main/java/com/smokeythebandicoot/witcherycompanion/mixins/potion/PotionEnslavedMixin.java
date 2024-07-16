package com.smokeythebandicoot.witcherycompanion.mixins.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.potion.PotionEnslaved;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix crash when Player name is null
 */
@Mixin(PotionEnslaved.class)
public class PotionEnslavedMixin {

    @SuppressWarnings("ConstantValue")
    @Inject(method = "setEnslaverForMob", remap = false, cancellable = true, at = @At("HEAD"))
    private static void setEnslaverForMob(EntityLiving entity, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.raising_fixNullPlayerName) {
            return;
        }

        if (player == null || player.getName() == null || player.getName().isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
