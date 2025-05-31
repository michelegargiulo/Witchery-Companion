package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing;


import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.brewing.UpgradableModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Modify Default Power Ceiling
 */
@Mixin(UpgradableModifier.class)
public abstract class UpgradeableModifierMixin {

    @ModifyConstant(method = "increase", remap = false, constant = @Constant(intValue = 7))
    private int modifyDefaultPowerCeiling(int constant) {
        return ModConfig.PatchesConfiguration.BrewsTweaks.common_tweakCustomPowerCeiling;
    }

}
