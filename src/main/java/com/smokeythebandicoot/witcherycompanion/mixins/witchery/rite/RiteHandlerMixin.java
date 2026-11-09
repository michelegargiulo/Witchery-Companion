package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.rite.RiteHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Configurable range that each witch of the coven adds to the range of a rite
 */
@Mixin(RiteHandler.class)
public interface RiteHandlerMixin {

    /**
     * Witchery allows a rite to be performed up to (coven size + 1) * 50 blocks from the circle. The 50 is the only
     * constant 50 of the method, and the default value of the config is 50, so by default nothing changes
     */
    @ModifyConstant(method = "run", remap = false, constant = @Constant(intValue = 50))
    default int witcherycompanion$riteRangePerWitch(int original) {
        return ModConfig.PatchesConfiguration.RitesTweaks.circle_tweakRiteRangePerWitch;
    }

}
