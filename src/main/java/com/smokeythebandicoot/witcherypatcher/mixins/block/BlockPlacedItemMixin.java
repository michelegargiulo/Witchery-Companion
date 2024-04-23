package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockPlacedItem;
import net.msrandom.witchery.block.entity.TileEntityPlacedItem;
import net.msrandom.witchery.init.WitcheryTileEntities;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 Mixins:
 [Bugfix] Fix Placed Items not dropping when Altar block is broken below them
 */
@Mixin(value = BlockPlacedItem.class, remap = false)
public class BlockPlacedItemMixin extends BlockContainer {


    private BlockPlacedItemMixin(Material materialIn) {
        super(materialIn);
    }

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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return WitcheryTileEntities.PLACED_ITEM.create();
    }

}
