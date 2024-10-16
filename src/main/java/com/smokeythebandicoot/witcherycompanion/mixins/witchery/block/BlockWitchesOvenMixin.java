package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.block.BlockWitchesOven;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * Mixins (Client):
 * [Bugfix] Fix burning particle spawning height
 *      Credits: @MsRandom
 *      Link: <a href="https://github.com/WitcheryResurrected/WitcheryResurrected/commit/b316a89d79b2516de9a54c53e47fa8d11ae9fb62">Relevant Commit</a>
 */
@Mixin(BlockWitchesOven.class)
public abstract class BlockWitchesOvenMixin {

    /** This Mixin wraps the call to world.nextDouble() to have an offset of 0.25 multiplied by 16 / 6 **/
    @WrapOperation(method = "randomDisplayTick", remap = true, at = @At(
            value = "INVOKE", target = "Ljava/util/Random;nextDouble()D", ordinal = 0))
    private double fixBurningParticlesHeight(Random instance, Operation<Double> original) {
        double result = original.call(instance);
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchesOven_fixBurningParticlesHeight) {
            return result + 0.67;
        }
        return result;
    }

}
