package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockGarlicGarland;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Mixins:
 * [Bugfix] Fix wrong facing upon placement
 */
@ParametersAreNonnullByDefault
@Mixin(BlockGarlicGarland.class)
public abstract class BlockGarlicGarlandMixin extends BlockContainer {

    @Shadow(remap = false) @Final @Mutable
    public static PropertyDirection FACING;

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB DEFAULT_AABB;

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB NORTH_AABB;

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB SOUTH_AABB;

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB WEST_AABB;

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB EAST_AABB;

    private BlockGarlicGarlandMixin(Material materialIn) {
        super(materialIn);
    }

    /**
     * This Mixin fixes the placement of the Garlic Garland to have the opposite facing when being placed
     **/
    @Inject(method = "onBlockPlacedBy", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixFacingOnPlacementPost(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.garlicGarland_fixFacingOnPlacement) {
            ci.cancel();
        }
    }

    @Inject(method = "<clinit>", remap = false, cancellable = true, at = @At("HEAD"))
    private static void fixAABB(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.garlicGarland_fixBoundingBox) {
            FACING = BlockHorizontal.FACING;
            DEFAULT_AABB = new AxisAlignedBB(0.25, 0.00, 0.25, 0.75, 0.5, 0.75);

            NORTH_AABB = new AxisAlignedBB(0.15, 0.80, 1.00, 0.85, 1.0, 0.95);
            SOUTH_AABB = new AxisAlignedBB(0.15, 0.80, 0.00, 0.85, 1.0, 0.05);

            WEST_AABB = new AxisAlignedBB(0.95, 0.80, 0.15, 0.95, 1.0, 0.85);
            EAST_AABB = new AxisAlignedBB(0.00, 0.80, 0.15, 0.05, 1.0, 0.85);
            ci.cancel();
        }
    }
}
