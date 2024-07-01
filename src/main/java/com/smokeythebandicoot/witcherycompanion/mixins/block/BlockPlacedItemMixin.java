package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockPlacedItem;
import net.msrandom.witchery.block.entity.TileEntityPlacedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

/**
 Mixins:
 [Bugfix] Fix Placed Items not dropping when Altar block is broken below them
 */
@Mixin(value = BlockPlacedItem.class)
public abstract class BlockPlacedItemMixin extends BlockContainer {

    private BlockPlacedItemMixin(Material materialIn) {
        super(materialIn);
    }

    @Shadow
    public abstract TileEntity createNewTileEntity(World worldIn, int meta);

    /** Injects the getDrops() function into the target class */
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.placedItems_fixNoDrops) {
            Random rand = world instanceof World ? ((World) world).rand : new java.util.Random();
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntity) {
                TileEntityPlacedItem placedItemTE = (TileEntityPlacedItem) te;
                drops.add(placedItemTE.getStack());
            }
        } else {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }


}
