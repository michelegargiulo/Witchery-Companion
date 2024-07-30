package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(BlockChest.class)
public abstract class BlockChestMixin extends BlockContainer implements ICursableTrigger {

    private BlockChestMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "onBlockActivated", remap = true, at = @At(value = "RETURN", ordinal = 1))
    private void injectTriggerOnChestOpen(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                          EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ,
                                          CallbackInfoReturnable<Boolean> cir) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof IProxedCursedTrigger))
            return;

        IProxedCursedTrigger proxedTrigger = (IProxedCursedTrigger) tile;
        proxedTrigger.onTrigger(playerIn);
    }
}
