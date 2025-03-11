package com.smokeythebandicoot.witcherycompanion.api;


import com.smokeythebandicoot.witcherycompanion.utils.ComparableItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SpectralFamiliarApi {

    private static HashMap<ComparableItemStack, IBlockState> oreMap = new HashMap<>();


    public static void addOre(IBlockState state, ItemStack... sniffs) {
        for (ItemStack stack : sniffs) {
            oreMap.put(new ComparableItemStack(stack), state);
        }
    }

    public static void removeOre(IBlockState state) {
        Iterator<Map.Entry<ComparableItemStack, IBlockState>> iterator = oreMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ComparableItemStack, IBlockState> entry = iterator.next();
            if (entry.getValue().equals(state)) {
                iterator.remove();
            }
        }
    }

    @Nullable
    public static IBlockState getOre(ItemStack stack) {
        return oreMap.getOrDefault(new ComparableItemStack(stack), null);
    }

    public static boolean hasOre(ItemStack stack) {
        return oreMap.containsKey(new ComparableItemStack(stack));
    }

}
