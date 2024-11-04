package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockWolfAltar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Bugfix] Fix Fences, Buttons etc. connecting to the wolf altar
 * [Bugfix] Fix AABB being too high causing players standing on the statue being kicked out of a server for flying
 */
@ParametersAreNonnullByDefault
@Mixin(BlockWolfAltar.class)
public abstract class BlockWolfAltarMixin extends BlockContainer {

    @Shadow(remap = false) @Final @Mutable
    private static AxisAlignedBB AABB;


    private BlockWolfAltarMixin(Material materialIn) {
        super(materialIn);
    }


    /** This Mixin returns BlockFaceShape undefined to avoid fences visually connecting to the statues.
     * Also buttons, levers, etc., except for the bottom side. **/
    @Override @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (BlockTweaks.wolfAltar_fixFaceShape) {
            return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
        return super.getBlockFaceShape(worldIn, state, pos, face);
    }

    /** This Mixin reduces the height of the AABB from 2.0 to 1.6, that is the max allowed by the server to avoid
     * being kicked from for flying **/
    @Inject(method = "<clinit>", remap = false, at = @At("TAIL"))
    private static void reduceAABBHeight(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.wolfAltar_fixFlyingOnServers) {
            AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.6, 1.0);
        }
    }

}