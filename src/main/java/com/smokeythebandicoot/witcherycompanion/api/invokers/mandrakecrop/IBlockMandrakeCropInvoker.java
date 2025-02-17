package com.smokeythebandicoot.witcherycompanion.api.invokers.mandrakecrop;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockMandrakeCropInvoker {

    void spawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack);

    boolean shouldSpawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack);

}
