package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityWitchProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Bugfix] Fix Ice Shield generation
 */
@Mixin(EntityWitchProjectile.class)
public abstract class EntityWitchProjectileMixin extends EntityThrowable {

    private EntityWitchProjectileMixin(World worldIn) {
        super(worldIn);
    }

    @Shadow(remap = false)
    private static boolean setBlockIfNotSolid(World world, BlockPos pos, Block block) {
        return false;
    }

    /** This Mixin fixes the generation of the Ice Column. Pos and Height are fixed parameters that never change
     * in the method, and witchery uses an iteration variable (offsetPosY) that is not used. The height variable
     * is used in the iteration instead, causing the blocks of ice to be set 1 block higher that it should be, 3 times (height times) **/
    @Inject(method = "explodeIceColumn", remap = false, at = @At("HEAD"), cancellable = true)
    private static void fixIceColumnGeneration(World world, BlockPos pos, int height, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.ItemTweaks.itemFrostBrew_fixIceColumn) {
            for(int offsetPosY = 0; offsetPosY < height; ++offsetPosY) {
                setBlockIfNotSolid(world, pos.up(offsetPosY), Blocks.ICE);
            }
            ci.cancel();
        }
    }

}
