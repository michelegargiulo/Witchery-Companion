package com.smokeythebandicoot.witcherypatcher.mixins.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockSunCollector;
import net.msrandom.witchery.init.WitcheryContainers;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 [Bugfix] Breaking Sun Collector while Quartz Sphere or Sun Granade won't void the item, but drop it
 */
@Mixin(value = BlockSunCollector.class, remap = false)
public abstract class BlockSunCollectorMixin extends Block {

    @Shadow public abstract int getMetaFromState(IBlockState state);

    private BlockSunCollectorMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);
        drops.add(new ItemStack(Item.getItemFromBlock(state.getBlock())));
        if (meta == 0) return;
        if (meta < 15) drops.add(new ItemStack(WitcheryIngredientItems.QUARTZ_SPHERE));
        if (meta == 15) drops.add(new ItemStack(WitcheryGeneralItems.SUN_GRENADE));
    }
}
