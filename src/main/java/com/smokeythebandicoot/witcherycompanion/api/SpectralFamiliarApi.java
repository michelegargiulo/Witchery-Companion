package com.smokeythebandicoot.witcherycompanion.api;


import com.smokeythebandicoot.witcherycompanion.utils.ComparableItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;


public class SpectralFamiliarApi {

    private static Map<ComparableItemStack, Set<IBlockState>> oreMap;


    static {
        initOreMap();
    }

    private static void initOreMap() {
         oreMap = new HashMap<>();

         IBlockState diamond = Blocks.DIAMOND_ORE.getDefaultState();
         IBlockState emerald = Blocks.EMERALD_ORE.getDefaultState();

         // Include ores that Witchery considers sittable by default
         addOre(new ItemStack(Items.COAL), Blocks.COAL_ORE.getDefaultState(), diamond, emerald);
         addOre(new ItemStack(Items.COAL), Blocks.IRON_ORE.getDefaultState(), diamond, emerald);
         addOre(new ItemStack(Items.GOLD_INGOT), Blocks.GOLD_ORE.getDefaultState(), diamond, emerald);
         addOre(new ItemStack(Items.DYE, 1, 4), Blocks.LAPIS_ORE.getDefaultState(), diamond, emerald);
         addOre(new ItemStack(Items.REDSTONE), Blocks.REDSTONE_ORE.getDefaultState(), diamond, emerald);

         // By default, Diamond and Emerald are always sittable
         addOre(new ItemStack(Items.DIAMOND), diamond, emerald);
         addOre(new ItemStack(Items.EMERALD), emerald, diamond);
    }

    /**  **/
    public static void addOre(ItemStack sniff, IBlockState... ores) {
        oreMap.computeIfAbsent(new ComparableItemStack(sniff), k -> new HashSet<>()).addAll(Arrays.asList(ores));
    }

    /**  **/
    public static void addOre(ItemStack sniff, Set<IBlockState> ores) {
        oreMap.computeIfAbsent(new ComparableItemStack(sniff), k -> new HashSet<>()).addAll(ores);
    }

    /**  **/
    public static void removeOre(ItemStack sniff, IBlockState state) {
        ComparableItemStack stack = new ComparableItemStack(sniff);
        if (oreMap.containsKey(stack)) {
            Set<IBlockState> ores = oreMap.get(stack);
            ores.remove(state);
            if (ores.isEmpty()) {
                oreMap.remove(stack);
            }
        }
    }

    /** Removes a block to find, the familiar will not find the block anymore **/
    public static void removeOre(IBlockState state) {
        Iterator<Map.Entry<ComparableItemStack, Set<IBlockState>>> iterator = oreMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ComparableItemStack, Set<IBlockState>> entry = iterator.next();
            Set<IBlockState> ores = entry.getValue();
            ores.remove(state);
            if (ores.isEmpty()) {
                iterator.remove();
            }
        }
    }

    /** The sniffed item won't trigger any block search **/
    public static void removeSniffable(ItemStack stack) {
        oreMap.remove(new ComparableItemStack(stack));
    }

    /** Returns the ore that the Familiar has to find for the given sniffed item **/
    @Nullable
    public static Set<IBlockState> getOre(ItemStack stack) {
        return oreMap.getOrDefault(new ComparableItemStack(stack), null);
    }

}
