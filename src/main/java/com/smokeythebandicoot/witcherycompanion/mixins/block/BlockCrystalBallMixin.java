package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.msrandom.witchery.block.BlockCrystalBall;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Configurable altar power for usage
 */
@Mixin(BlockCrystalBall.class)
public abstract class BlockCrystalBallMixin extends BlockContainer {

    private BlockCrystalBallMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin changes the hardcoded constant of 500 Altar power for a Crystal Ball prediction **/
    @ModifyConstant(method = "tryConsumePower", remap = false, constant = @Constant(floatValue = 500.0f))
    private static float modifyRequiredPower(float constant) {
        return (float) ModConfig.PatchesConfiguration.BlockTweaks.crystalBall_tweakRequiredPower;
    }
}
