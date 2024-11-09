package com.smokeythebandicoot.witcherycompanion.api.accessors.kettle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ITileEntityKettleAccessor {

    boolean getIsRuined();

    float getCurrentPowerNeeded();

    NonNullList<ItemStack> getItems();

    String requiredFamiliar();

    boolean satisfyFamiliar(EntityPlayer player);

    Integer requiredDimension();
}
