package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing.action;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.brewing.action.BrewActionRitual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Configurable factor of the range that each witch of the coven adds to the range of a Brew Ritual
 */
@Mixin(BrewActionRitual.class)
public abstract class BrewActionRitualMixin {

    /**
     * Witchery allows a Brew Ritual to be cast from the circle up to 2 * ((coven size + 1) * 4)^2 blocks away, when the
     * coven has less than 6 witches. The 4 is the only constant 4 of the method, and the default value of the config is 4, so
     * by default nothing changes
     */
    @ModifyConstant(method = "isDistanceAllowed", remap = false, constant = @Constant(intValue = 4))
    private static int witcherycompanion$brewRitualRangeFactor(int original) {
        return ModConfig.PatchesConfiguration.BrewsTweaks.brewRitual_tweakRangeFactorPerWitch;
    }

}
