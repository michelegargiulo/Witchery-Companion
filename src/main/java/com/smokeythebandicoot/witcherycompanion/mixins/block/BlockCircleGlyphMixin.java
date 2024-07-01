package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.block.BlockCircleGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Out of Bounds crash when trying to retrieve state from meta using invalid meta
 */
@Mixin(BlockCircleGlyph.class)
public abstract class BlockCircleGlyphMixin extends Block {

    private BlockCircleGlyphMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    /** This Mixin checks if the meta param is within the bounds of the possible values. If it is, it does nothing,
     otherwise it returns the getDefaultState() */
    @Inject(method = "getStateFromMeta", remap = true, cancellable = true, at = @At("HEAD"))
    public void fixOutOfBoundsMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.circleGlyph_fixOutOfBoundsCrash) {
            // If the state is outside the possible values, return default state
            if (meta < 0 || meta > 11) {
                cir.setReturnValue(this.getDefaultState());
            }
        }
    }

}
