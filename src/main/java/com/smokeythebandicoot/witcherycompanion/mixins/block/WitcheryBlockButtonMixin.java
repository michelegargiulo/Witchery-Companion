package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.WitcheryBlockButton;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(WitcheryBlockButton.class)
public abstract class WitcheryBlockButtonMixin extends BlockContainer implements ICursableTrigger {

    @Shadow(remap = false) @Final
    private static PropertyBool POWERED;

    @Shadow(remap = false)
    protected abstract void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos);

    @Shadow(remap = false)
    protected abstract void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing);

    @Shadow(remap = false) @Final
    private static PropertyDirection FACING;

    private WitcheryBlockButtonMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin prevents the retrieval of the TileEntityCursedBlock that should exist in place of this block
     * and instead refers to the ICursableTrigger interface to trigger curse effects */
    @Inject(method = "onBlockActivated", remap = true, cancellable = true, at = @At("HEAD"))
    private void injectOnActivate(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (!TriggeredDispersalTweaks.enable_dispersalRework || !TriggeredDispersalTweaks.enable_button) {
            return;
        }
        if (!(Boolean)state.getValue(POWERED)) {

            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(POWERED, true), 3);
                this.onTrigger(world, pos, playerIn);
                world.markBlockRangeForRenderUpdate(pos, pos);
            }

            this.playClickSound(playerIn, world, pos);
            this.notifyNeighbors(world, pos, state.getValue(FACING));
            world.scheduleUpdate(pos, this, this.tickRate(world));
        }

        cir.setReturnValue(true);
    }

    /** This Mixin prevents any TileEntity from being created when the 'createTileEntity' function is executed */
    @Inject(method = "createNewTileEntity", remap = true, cancellable = true, at = @At("HEAD"))
    private void preventCreateNewTileEntity(World worldIn, int meta, CallbackInfoReturnable<TileEntity> cir) {
        if (!TriggeredDispersalTweaks.enable_dispersalRework || !TriggeredDispersalTweaks.enable_button) {
            cir.setReturnValue(null);
        }
    }

    /** This Mixin prevents any code in the 'replaceButton' function from being executed */
    @Inject(method = "replaceButton", remap = false, cancellable = true, at = @At("HEAD"))
    private void preventReplaceButton(World world, BlockPos pos, ModifiersImpact impactModifiers, BrewActionList actionList, CallbackInfo ci) {
        if (!TriggeredDispersalTweaks.enable_dispersalRework || !TriggeredDispersalTweaks.enable_button) {
            ci.cancel();
        }
    }
}
