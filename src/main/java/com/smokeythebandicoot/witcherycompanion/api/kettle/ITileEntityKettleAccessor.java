package com.smokeythebandicoot.witcherycompanion.api.kettle;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ITileEntityKettleAccessor {

    boolean accessor_getIsRuined();

    float accessor_getCurrentPowerNeeded();

    NonNullList<ItemStack> accessor_getItems();
}
