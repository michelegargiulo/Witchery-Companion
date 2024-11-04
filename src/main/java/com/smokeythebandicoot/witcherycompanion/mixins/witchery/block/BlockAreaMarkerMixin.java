package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.msrandom.witchery.block.BlockAreaMarker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Tweak AABB to be tighter around the statue
 */
@Mixin(BlockAreaMarker.class)
public abstract class BlockAreaMarkerMixin extends BlockContainer {

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB AABB;

    private BlockAreaMarkerMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin modifies the default AABB of the statues **/
    @Inject(method = "<clinit>", remap = false, at = @At("TAIL"))
    private static void reduceAABBHeight(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.creativeStatues_tweakTightenBoundingBoxes) {
            AABB = new AxisAlignedBB(0.35, 0.0, 0.35, 0.65, 0.45, 0.65);
        }
    }
}
