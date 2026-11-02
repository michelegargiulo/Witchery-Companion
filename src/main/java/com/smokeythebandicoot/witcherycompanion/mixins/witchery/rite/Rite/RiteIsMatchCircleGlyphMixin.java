package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.Rite;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import kotlin.jvm.internal.Ref;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Incomplete circles were recognized as complete. Rite.isMatch() walks the ring of a circle with a function that
 *      remembers the glyph of the first block found and returns false (stopping the walk) at the first gap or different block.
 *      A different block clears the remembered glyph, but a gap (air) does not, and isMatch() only checks that a glyph was
 *      remembered: a ring with a single chalk in the first position visited was accepted. Any false result now clears the glyph.
 *
 * Target: the lambda that Kotlin generates for the inlined getCircleGlyph() in Rite.isMatch()
 */
@Mixin(targets = "net.msrandom.witchery.rite.Rite$isMatch$$inlined$getCircleGlyph$1")
public abstract class RiteIsMatchCircleGlyphMixin {

    @Shadow(remap = false) @Final
    private Ref.ObjectRef<Block> $block;

    @Inject(method = "invoke(Lnet/minecraft/util/math/BlockPos;)Z", remap = false, at = @At("RETURN"))
    private void witcherycompanion$clearGlyphOnGap(BlockPos position, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.PatchesConfiguration.RitesTweaks.circle_fixIncompleteCirclesRecognized) return;

        if (!cir.getReturnValue()) {
            this.$block.element = null;
        }
    }

}
