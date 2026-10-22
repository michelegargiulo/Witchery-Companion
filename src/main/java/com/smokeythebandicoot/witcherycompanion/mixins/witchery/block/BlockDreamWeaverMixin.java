package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockDreamWeaver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Fix placement facing
 */
@Mixin(BlockDreamWeaver.class)
public abstract class BlockDreamWeaverMixin extends BlockContainer {

    @Shadow(remap = false) @Final
    private static PropertyDirection FACING;

    private BlockDreamWeaverMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin fixes the facing on placement to not be the player facing, but rather the block's **/
    @Inject(method = "getStateForPlacement", remap = true, at = @At("HEAD"), cancellable = true)
    private void fixPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, CallbackInfoReturnable<IBlockState> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.DreamweaverTweaks.fixPlacement) {
            return;
        }

        IBlockState state = this.getDefaultState();
        if (facing.getAxis().isHorizontal()) {
            cir.setReturnValue(state.withProperty(FACING, facing));
        }
        else {
            cir.setReturnValue(state);
        }
    }

    /** This Mixin prevents the 'onBlockPlacedBy' function to overwrite the result of 'getStateForPlacement' **/
    @Inject(method = "onBlockPlacedBy", remap = true, at = @At("HEAD"), cancellable = true)
    private void fixPlacement(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.DreamweaverTweaks.fixPlacement) {
            return;
        }
        ci.cancel();
    }

    /** This Mixin overrides the canPlaceBlockOnSide to perform additional checks **/
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.DreamweaverTweaks.fixPlacement) {
            return super.canPlaceBlockOnSide(worldIn, pos, side);
        }
        return side.getAxis().isHorizontal() && // Axis must be horizontal
                worldIn.isSideSolid(pos.offset(side.getOpposite()), side) && // There must be a support behing
                !(worldIn.getBlockState(pos).getBlock() instanceof BlockDreamWeaver); // The block must not be already occupied by a dreamweaver
    }

}
