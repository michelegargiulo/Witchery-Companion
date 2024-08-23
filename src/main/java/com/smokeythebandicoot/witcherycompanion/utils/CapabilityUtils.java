package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;

public class CapabilityUtils {

    // Defined format for Capacity Brew secret items: MODID/brewing/capacity/<namespace>:<path>:<meta>
    public static String getBrewingCapacitySecret(ItemStack stack) {
        if (stack == null)
            return null;
        return WitcheryCompanion.MODID + "/brewing/capacity/" + stack.getItem().getRegistryName() + ":" + stack.getMetadata();
    }

}
