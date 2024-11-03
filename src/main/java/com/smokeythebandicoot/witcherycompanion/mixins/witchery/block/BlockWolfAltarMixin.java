package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockWolfAltar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Bugfix] Fix Fences, Buttons etc. connecting to the wolf altar
 */
@ParametersAreNonnullByDefault
@Mixin(BlockWolfAltar.class)
public abstract class BlockWolfAltarMixin extends BlockContainer {

    @Shadow(remap = false) @Final
    private static PropertyDirection FACING;

    //private static PropertyBool UPPER_HALF = PropertyBool.create("is_upper_half");


    private BlockWolfAltarMixin(Material materialIn) {
        super(materialIn);
    }


    /** This Mixin returns BlockFaceShape undefined to avoid fences visually connecting to the statues.
     * Also buttons, levers, etc., except for the bottom side. **/
    @Override @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.wolfAltar_fixFaceShape) {
            return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
        return super.getBlockFaceShape(worldIn, state, pos, face);
    }

    /*@Inject(method = "<init>", remap = false, at = @At("RETURN"))
    private void setUpperHalfOnInit(CallbackInfo ci) {

    }*/



    /*@Override
    public boolean hasTileEntity(IBlockState state) {
        return !state.getValue(UPPER_HALF);
    }*/

    /** This Mixin states that the statue must be placed as the lower half **/
    /*@Inject(method = "getStateForPlacement", remap = true, cancellable = true, at = @At("HEAD"))
    public void getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, CallbackInfoReturnable<IBlockState> cir) {
        if (false) {
            cir.setReturnValue(this.getDefaultState()
                    .withProperty(UPPER_HALF, false)
                    .withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
        }
    }*/

    /*public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }*/
}
