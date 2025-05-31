package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.kettle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ITileEntityKettleAccessor {

    boolean witcherycompanion$accessor$getIsRuined();

    float witcherycompanion$accessor$getCurrentPowerNeeded();

    NonNullList<ItemStack> witcherycompanion$accessor$getItems();

    String witcherycompanion$accessor$requiredFamiliar();

    boolean witcherycompanion$accessor$satisfyFamiliar(EntityPlayer player);

    Integer witcherycompanion$accessor$requiredDimension();
}
