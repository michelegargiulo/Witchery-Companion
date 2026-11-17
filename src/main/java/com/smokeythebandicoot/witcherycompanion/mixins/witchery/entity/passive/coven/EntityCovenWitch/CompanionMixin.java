package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.passive.coven.EntityCovenWitch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.CovenSummonPositions;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Bugfix] Crash (ArrayIndexOutOfBoundsException) when a circle summons more than 6 Coven Witches
 */
@Mixin(EntityCovenWitch.Companion.class)
public class CompanionMixin {

    /**
     * Witchery reads the facing of the witch being summoned from an array of 6 entries, with the index of the witch.
     * The array is replaced by one with the same first entries and more facings (see TileEntityCircleMixin for the
     * positions)
     */
    @ModifyExpressionValue(method = "processCovenSummon", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/entity/passive/coven/EntityCovenWitch;access$getFACING$cp()[F"))
    private float[] witcherycompanion$extendCovenWitchFacings(float[] original) {
        if (ModConfig.PatchesConfiguration.RitesTweaks.circle_fixCovenSummonPositions) {
            return CovenSummonPositions.extendFacings(original);
        }
        return original;
    }

}
