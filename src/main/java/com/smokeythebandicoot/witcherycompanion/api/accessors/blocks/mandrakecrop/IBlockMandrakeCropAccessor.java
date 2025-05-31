package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.mandrakecrop;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockMandrakeCropAccessor {

    void witcherycompanion$accessor$spawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack);

    boolean witcherycompanion$accessor$shouldSpawnMandrake(World world, EntityPlayer player, BlockPos pos, IBlockState state, ItemStack stack);

}
