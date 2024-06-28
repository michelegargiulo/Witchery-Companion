package com.smokeythebandicoot.witcherycompanion.mixins.rite.effect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.ErosionBrewApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.RitesTweaks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.rite.effect.RiteEffect;
import net.msrandom.witchery.rite.effect.RiteEffectPartEarth;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] (Partial, complementary to RiteEffectPartEarth_DirectionMixin) Fix NPE when destination location is null
 */
@Mixin(RiteEffectPartEarth.class)
public abstract class RiteEffectPartEarthMixin extends RiteEffect {

    /** This Mixin controls what blocks can be replaced to air based on the destroy and mine list of Brew of Erosion,
     since the ritual foci is Brew of Erosion item */
    @WrapOperation(method = "drawPixel", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/WitcheryUtils;canBreak(Lnet/minecraft/block/state/IBlockState;)Z"))
    protected boolean doMoreBreakableChecks(IBlockState state, Operation<Boolean> original) {
        if (RitesTweaks.brokenEarth_tweakAlignBreakablesWithErosion) {
            return ErosionBrewApi.canAffect(state);
        }
        return original.call(state);
    }


    /** This Mixin is a partial fix on a NullPointerException that happens because sometimes the direction of the rite
     could not be determined properly. In the process() function of RiteEffectPartEarth, the ritual.sacrificedItems.get(this.foci)
     might return null, and the getLocation() produces an NPE. This Mixin fixes that by returning NULL. This would result
     in the same crash but in the RiteEffectPartEarth$Direction enum. Such issue is fixed by the complement mixin */
    @WrapOperation(method = "process", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/rite/effect/RiteEffect$SacrificedItem;getLocation()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos fixNPEOnGetFoci(RiteEffect.SacrificedItem instance, Operation<BlockPos> original) {
        if (RitesTweaks.brokenEarth_fixNPEOnNullFociLocation && instance == null) {
            return null;
        }
        return original.call(instance);
    }

}
