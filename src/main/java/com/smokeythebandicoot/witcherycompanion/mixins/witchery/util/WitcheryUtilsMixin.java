package com.smokeythebandicoot.witcherycompanion.mixins.witchery.util;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.world.World;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitcheryUtils.class)
public abstract class WitcheryUtilsMixin {

    @Inject(method = "isFullMoon", remap = false, at = @At("HEAD"), cancellable = true)
    private static void isFullMoonDimension(World world, CallbackInfoReturnable<Boolean> cir) {

        int dimID = world.provider.getDimension();

        // Is eternal full moon dimension, then it is always transformed
        if (witcherycompanion$isAlwaysWerewolfDimension(dimID)) {
            cir.setReturnValue(true);
            return;
        }

        // Is eternal sun dimension, then it is never transformed
        if (witcherycompanion$isNeverWerewolfDimension(dimID)) {
            cir.setReturnValue(false);
            return;
        }

        // Else, proceed as normal by checking full moon and daytime
        cir.setReturnValue(world.getCurrentMoonPhaseFactor() == 1.0f && !world.isDaytime());
    }

    @Unique
    private static boolean witcherycompanion$isAlwaysWerewolfDimension(int dim) {
        for (int dimId : ModConfig.PatchesConfiguration.TransformationTweaks.werewolf_alwaysFullMoonDimensions) {
            if (dimId == dim) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean witcherycompanion$isNeverWerewolfDimension(int dim) {
        for (int dimId : ModConfig.PatchesConfiguration.TransformationTweaks.werewolf_neverFullMoonDimensions) {
            if (dimId == dim) {
                return true;
            }
        }
        return false;
    }

}
