package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAltar.class)
public abstract class BlockAltarMixin extends BlockContainer {

    @Shadow(remap = false)
    protected abstract BlockAltar.AltarPatternMatch findParts(IBlockAccess world, BlockPos pos);

    @Shadow(remap = false)
    protected abstract BlockPos getCore(IBlockAccess world, BlockPos pos);

    private BlockAltarMixin(Material materialIn) {
        super(materialIn);
    }

    @Unique
    private BlockAltar.AltarPatternMatch witchery_Patcher$preservedPattern = null;

    @Inject(method = "getCore", remap = false, cancellable = true, at = @At("HEAD"))
    public void fixedGetCore(IBlockAccess world, BlockPos pos, CallbackInfoReturnable<BlockPos> cir) {
        BlockAltar.AltarPatternMatch altarPattern = this.findParts(world, pos);
        BlockPos corePos;
        if (altarPattern != null) {
            corePos = altarPattern.getParts().inverse().get(BlockAltar.Part.FRONT_MIDDLE);
        } else {
            corePos = null;
        }

        witchery_Patcher$preservedPattern = altarPattern;
        cir.setReturnValue(corePos);
    }

    @Inject(method = "neighborChanged", remap = true, cancellable = true, at = @At("HEAD"))
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci) {

        if (!world.isRemote) {
            // Call getCore, to re-compute Altar Pattern
            BlockPos corePos = this.getCore(world, pos);
            TileEntityAltar coreAltar = null;

            // If core is found, meaning that pattern is correct, then update TEs
            if (corePos != null) {
                coreAltar = WitcheryTileEntities.ALTAR.getAt(world, corePos);
            }

            // If a CoreAltar has been found, then invalidate all other Altars
            if (coreAltar != null) {
                coreAltar.updateArtifacts();
                for (BlockAltar.Part part : witchery_Patcher$preservedPattern.getParts().values()) {
                    BlockPos partPos = witchery_Patcher$preservedPattern.getParts().inverse().get(part);
                    TileEntity tile = world.getTileEntity(partPos);
                    if (!(tile instanceof TileEntityAltar)) continue;
                    TileEntityAltar altarPart = (TileEntityAltar) tile;
                    if (partPos.equals(corePos)) {
                        ReflectionHelper.setField(altarPart, "core", false, true);
                        altarPart.updatePower();
                    } else {
                        altarPart.setInvalid();
                    }
                }
            }
        }

    }

}
