package com.smokeythebandicoot.witcherycompanion.mixins.witchery.world.gen.structure;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.world.World;
import net.msrandom.witchery.world.gen.structure.ChunkPointsOfInterest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Bugfix] Fix Cubic Chunks incompatibility
 */
@Mixin(ChunkPointsOfInterest.class)
public abstract class ChunkPointsOfInterestMixin {

    /** This Mixin injects into the world.getHeight() function. In vanilla, it always returns 256. Some mods
     * like CubicChunks might increase it to much higher values. Since Witchery creates an array of elements with size
     * depending on world height, for truly massive world heights an Out Of Memory error is thrown. This mixin forces
     * the getHeight() function to return vanilla world height **/
    @WrapOperation(method = "getSections", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getHeight()I", remap = true))
    private int addCubicChunksCompat(World instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.WorldGenTweaks.chunkPOI_fixCubicChunksIncompat) {
            return 256;
        }
        return original.call(instance);
    }

}
