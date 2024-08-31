package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.api.altar.IBlockAltarAccessor;
import com.smokeythebandicoot.witcherycompanion.api.altar.ITileEntityAltarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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

/**
 Mixins:
 [Bugfix] Fix Altar having multiple Cores (has same config as altar_fixPowerSourcePersistency)
 */
@Mixin(BlockAltar.class)
public abstract class BlockAltarMixin extends BlockContainer implements IBlockAltarAccessor {

    @Shadow(remap = false)
    protected abstract BlockAltar.AltarPatternMatch findParts(IBlockAccess world, BlockPos pos);

    @Shadow(remap = false)
    protected abstract BlockPos getCore(IBlockAccess world, BlockPos pos);

    private BlockAltarMixin(Material materialIn) {
        super(materialIn);
    }

    @Unique
    private BlockAltar.AltarPatternMatch witchery_Patcher$preservedPattern = null;

    /** This Mixin catches the Altar Pattern computed by findParts, for later use in onNeighborChanged */
    @Inject(method = "getCore", remap = false, cancellable = true, at = @At("HEAD"))
    public void fixedGetCore(IBlockAccess world, BlockPos pos, CallbackInfoReturnable<BlockPos> cir) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
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
    }

    /** This Mixin validates the Core TE and invalidates all the others, including non-neighbors */
    @Inject(method = "neighborChanged", remap = true, cancellable = true, at = @At("HEAD"))
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci) {

        if (BlockTweaks.altar_fixPowerSourcePersistency) {
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
                    for (BlockAltar.Part part : witchery_Patcher$preservedPattern.getParts().values()) {
                        BlockPos partPos = witchery_Patcher$preservedPattern.getParts().inverse().get(part);
                        TileEntity tile = world.getTileEntity(partPos);
                        if (!(tile instanceof TileEntityAltar)) continue;
                        TileEntityAltar altarPart = (TileEntityAltar) tile;
                        if (partPos.equals(corePos)) {
                            // Use Accessor to set core to true for the part
                            ITileEntityAltarAccessor altarAccessor = (ITileEntityAltarAccessor)altarPart;
                            altarAccessor.accessor_setCore(true);
                            altarPart.updatePower();
                        } else {
                            altarPart.setInvalid();
                        }
                    }
                } else {
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof TileEntityAltar) {
                        TileEntityAltar altar = (TileEntityAltar) te;
                        altar.setInvalid();
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "onBlockPlacedBy", remap = true, at = @At("TAIL"))
    public void onPlaceSetInvalid(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, CallbackInfo ci) {
        if (BlockTweaks.altar_fixPowerSourcePersistency) {
            if (world.isRemote) return;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityAltar) {
                TileEntityAltar altar = (TileEntityAltar) te;
                altar.setInvalid();
            }
        }
    }

    /** Accessor Mixin for the getCore function. Used by The One Probe */
    @Override
    public BlockPos accessor_getCore(IBlockAccess world, BlockPos pos) {
        return getCore(world ,pos);
    }

}
