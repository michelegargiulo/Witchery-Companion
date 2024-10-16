package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.effect.RiteEffectPartEarth;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] (Partial, complementary to RiteEffectPartEarthMixin) Fix NPE when destination location is null
 */
@Mixin(targets = "net/msrandom/witchery/rite/effect/RiteEffectPartEarth$Direction")
public class DirectionMixin {

    /** This Mixin prevents an NPE from occurring when companion passes NULL as destination param. This is due to
     Companion fixing another NPE. See RiteEffectPartEarthMixin.
     NOTE: unchecked and rawtypes warnings are suppressed due to the usage of un-parametrized CallbackInfoReturnable
     class due to RiteEffectPartEarth$Direction having private access */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "getFromHeading", remap = false, at = @At("HEAD"), cancellable = true)
    private static void fixNpeOnNullLocation(BlockPos original, BlockPos destination, CallbackInfoReturnable cir) {
        if (ModConfig.PatchesConfiguration.RitesTweaks.brokenEarth_fixNPEOnNullFociLocation && (original == null || destination == null)) {
            cir.setReturnValue(null);
        }
    }

}
