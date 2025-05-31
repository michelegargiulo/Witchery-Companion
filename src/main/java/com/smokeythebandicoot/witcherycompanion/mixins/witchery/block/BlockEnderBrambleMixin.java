package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.block.BlockEnderBramble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Modify min and max teleportation distance
 */
@Mixin(BlockEnderBramble.class)
public abstract class BlockEnderBrambleMixin {

    @ModifyConstant(method = "onEntityCollision", remap = false, constant = @Constant(intValue = 500))
    private int tweakMinDistance(int constant) {
        return ModConfig.PatchesConfiguration.BlockTweaks.enderBramble_tweakMinDistanceTP;
    }

    @ModifyConstant(method = "onEntityCollision", remap = false, constant = @Constant(intValue = 1000))
    private int tweakMaxDistance(int constant) {
        return ModConfig.PatchesConfiguration.BlockTweaks.enderBramble_tweakMaxDistanceTP;
    }

}
