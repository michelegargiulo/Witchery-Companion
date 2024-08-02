package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(BlockWorkbench.class)
public abstract class BlockWorkbenchMixin extends Block implements ICursableTrigger {

    private BlockWorkbenchMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "onBlockActivated", remap = true, at = @At(value = "HEAD"))
    private void triggerOnFlipOnBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (!worldIn.isRemote &&
            TriggeredDispersalTweaks.enable_dispersalRework &&
            TriggeredDispersalTweaks.enable_craftingTable) {
            this.onTrigger(worldIn, pos, playerIn);
        }
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }
}
