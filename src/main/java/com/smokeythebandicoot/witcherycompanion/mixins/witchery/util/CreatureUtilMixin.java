package com.smokeythebandicoot.witcherycompanion.mixins.witchery.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.TransformationTweaks;
import net.minecraft.entity.Entity;
import net.msrandom.witchery.util.CreatureUtil;
import net.msrandom.witchery.world.dimension.WitcheryDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Always-night dimensions
 */
@Mixin(CreatureUtil.class)
public abstract class CreatureUtilMixin {

    /** Injects into the first call to isInDimension and returns true if the entity is in an alwaysNightDimension.
     * Note that the result of the original operation is negated, so this mixin should actually check if the
     * entity is in an always-night dimension (just like Spirit and Torment) **/
    @WrapOperation(method = "isInSunlight", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/world/dimension/WitcheryDimension;isInDimension(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean tweakConsiderAlwaysNightDimensions(WitcheryDimension instance, Entity entity, Operation<Boolean> original) {
        return original.call(instance, entity) || witcherycompanion$isInNightDimension(entity.getEntityWorld().provider.getDimension());
    }

    @Unique
    private static boolean witcherycompanion$isInNightDimension(int dim) {
        for (int dimID : TransformationTweaks.vampire_alwaysNightDimensions) {
            if (dim == dimID) {
                return true;
            }
        }
        return false;
    }

}
