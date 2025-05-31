package com.smokeythebandicoot.witcherycompanion.mixins.witchery.transformation.CreatureForm;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.transformation.CreatureForm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CreatureForm.Stats.class)
public abstract class StatsMixin {

    @ModifyConstant(method = "<init>", remap = false, constant = @Constant(floatValue = 0.5f))
    private float modifyDefaultStepHeight(float constant) {
        if (ModConfig.PatchesConfiguration.TransformationTweaks.noForm_fixDefaultStepHeight) {
            return 0.6f;
        }
        return 0.5f;
    }

}
