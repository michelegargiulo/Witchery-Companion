package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockStatueOfWorship;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Tweak] Shrink AABB
 * [Bugfix] Fix placing held block when right-clicking the statue
 */
@ParametersAreNonnullByDefault
@Mixin(BlockStatueOfWorship.class)
public abstract class BlockStatueOfWorshipMixin extends BlockContainer {

    private BlockStatueOfWorshipMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin shrinks the AABB to be tighter around the statue **/
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.15, 0.75);
    }

    /** This Mixin injects after the second call to WitcheryNetworkChannel.sendToAllAround() and returns true instead of false **/
    @Inject(method = "onBlockActivated", remap = false, cancellable = true, at = @At(value = "RETURN"))
    private void preventBlockPlacingOnRightClick(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.hobgoblinPatronStatue_fixBlockPlacing) {
            cir.setReturnValue(true);
        }
    }


}
