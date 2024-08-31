package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockKettle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix using brewed potions immediately upon creation
 */
@Mixin(BlockKettle.class)
public abstract class BlockKettleMixin extends BlockContainer {

    private BlockKettleMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin avoids to return false at the end of the method */
    @Inject(method = "onBlockActivated", remap = true, cancellable = true, at = @At(value = "HEAD"))
    private void callSuperOnBlockActivation(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.kettle_fixThrowBrewsUponCreation) {
            if (world.isRemote) cir.setReturnValue(true);
        }
    }
}
