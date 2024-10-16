package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockBeartrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Tweak] Beartrap will drop if there is no floor underneath
 * [Tweak] Wolf Trap can be broken with a pickaxe and drops itself for relocation
 */
@ParametersAreNonnullByDefault
@Mixin(BlockBeartrap.class)
public abstract class BlockBeartrapMixin extends BlockContainer {

    private BlockBeartrapMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin checks if the trap has a solid surface underneath, and if not, drops the block as item **/
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.bearTrap_tweakNeedsSolidSurface &&
                !world.isSideSolid(pos.down(), EnumFacing.UP, true)) {

            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    /** This Mixin injects intot he getDrops function, that checks if the trap is silvered and, if not, drops. This Mixin
     * makes it so that the call to super.getDrops() is always executed if the tweak is active **/
    @Inject(method = "getDrops", remap = false, cancellable = true, at = @At("HEAD"))
    public void dropEvenWhenSilvered(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.wolfTrap_tweakDropOnBreak) {
            super.getDrops(drops, world, pos, state, fortune);
            ci.cancel();
        }
    }
}
