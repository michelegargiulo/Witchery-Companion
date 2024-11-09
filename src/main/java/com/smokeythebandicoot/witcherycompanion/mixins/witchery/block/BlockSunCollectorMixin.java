package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.suncollector.IBlockSunCollectorAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockSunCollector;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 Mixins:
 [Bugfix] Breaking Sun Collector while Quartz Sphere or Sun Granade won't void the item, but drop it
 */
@Mixin(value = BlockSunCollector.class)
public abstract class BlockSunCollectorMixin extends Block implements IBlockSunCollectorAccessor {

    @Shadow(remap = true)
    public abstract int getMetaFromState(IBlockState state);

    @Shadow(remap = false) @Final
    private static PropertyInteger POWER;

    private BlockSunCollectorMixin(Material materialIn) {
        super(materialIn);
    }


    /** Injects the getDrops() function into the target class */
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = this.getMetaFromState(state);
        drops.add(new ItemStack(Item.getItemFromBlock(state.getBlock())));
        if (meta == 0) return;
        if (meta < 15) drops.add(new ItemStack(WitcheryIngredientItems.QUARTZ_SPHERE));
        if (meta == 15) drops.add(new ItemStack(WitcheryGeneralItems.SUN_GRENADE));
    }

    @Override
    public int getPower(IBlockState state) {
        return state.getValue(POWER);
    }
}
