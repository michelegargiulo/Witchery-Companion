package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.RiteConditions;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.rite.RiteConditions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiPredicate;

/**
 * Mixins:
 * [Bugfix] Negated rite conditions always passed. W:R evaluates "predicate OR negated OR orCondition", so the negation
 *      is never applied: a condition with "negate": true is always true. Effect: Rite of Total Eclipse, whose data
 *      negates the "night" condition, could be performed at night. The fixed logic is "(predicate != negated) OR orCondition"
 */
@Mixin(RiteConditions.RiteCondition.class)
public abstract class RiteConditionMixin {

    @Shadow(remap = false) @Final
    private BiPredicate<World, BlockPos> predicate;

    @Shadow(remap = false)
    private boolean negated;

    @Shadow(remap = false)
    private RiteConditions.RiteCondition orCondition;

    @Inject(method = "invoke(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Boolean;",
            remap = false, at = @At("HEAD"), cancellable = true)
    private void witcherycompanion$fixNegation(World world, BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.PatchesConfiguration.RitesTweaks.riteConditions_fixNegatedConditions) return;

        boolean result = this.predicate.test(world, position) != this.negated;
        if (!result && this.orCondition != null) {
            result = this.orCondition.invoke(world, position);
        }
        cir.setReturnValue(result);
    }

}
