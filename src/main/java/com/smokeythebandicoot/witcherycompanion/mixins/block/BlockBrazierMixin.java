package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.api.brazier.ITileEntityBrazierAccessor;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockBrazier;
import net.msrandom.witchery.block.entity.TileEntityBrazier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Unlock secret brazier recipes
 */
@Mixin(BlockBrazier.class)
public abstract class BlockBrazierMixin extends BlockContainer {

    private BlockBrazierMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin stores the player who ignited the brazier as the Recipe Owner. If any secrets will be revealed (an
     * hidden recipe is activated) then this player will unlock the secret. TileEntityBrazier will reset the recipe owner */
    @Inject(method = "onBlockActivated", at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityBrazier;begin()V"))
    private void setRecipeOwner(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITileEntityBrazierAccessor) {
            ITileEntityBrazierAccessor brazier = (ITileEntityBrazierAccessor) tile;
            brazier.setRecipeOwner(player);
        }
    }
}
