package com.smokeythebandicoot.witcherycompanion.integrations.api;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class InfernalImpApi {

    public static HashMap<ItemStack, Integer> shinies = new HashMap<>();

    static {
        shinies.put(new ItemStack(Items.DIAMOND), 8);
        shinies.put(new ItemStack(Items.DIAMOND_AXE), 24);
        shinies.put(new ItemStack(Items.DIAMOND_HOE), 16);
        shinies.put(new ItemStack(Items.DIAMOND_SWORD), 16);
        shinies.put(new ItemStack(Items.DIAMOND_SHOVEL), 8);
        shinies.put(new ItemStack(Items.DIAMOND_PICKAXE), 24);
        shinies.put(new ItemStack(Items.EMERALD), 3);
        shinies.put(new ItemStack(Items.GOLD_INGOT), 1);
        shinies.put(new ItemStack(Items.NETHER_STAR), 16);
        shinies.put(new ItemStack(Items.BLAZE_ROD), 1);
        shinies.put(new ItemStack(Items.GHAST_TEAR), 4);
        shinies.put(new ItemStack(Items.GOLDEN_AXE), 3);
        shinies.put(new ItemStack(Items.GOLDEN_SWORD), 2);
        shinies.put(new ItemStack(Items.GOLDEN_HOE), 2);
        shinies.put(new ItemStack(Items.GOLDEN_SHOVEL), 1);
        shinies.put(new ItemStack(Items.GOLDEN_PICKAXE), 3);
        shinies.put(new ItemStack(Blocks.GOLD_BLOCK), 9);
        shinies.put(new ItemStack(Blocks.EMERALD_BLOCK), 27);
        shinies.put(new ItemStack(Blocks.DIAMOND_BLOCK), 72);
        shinies.put(new ItemStack(Blocks.LAPIS_BLOCK), 7);
        shinies.put(new ItemStack(Blocks.REDSTONE_BLOCK), 5);
    }

    public static boolean addShiny(ItemStack shiny, int affectionBoost) {
        if (affectionBoost <= 0) return false;
        return shinies.put(simplifyStack(shiny), affectionBoost) == null;
    }

    public static boolean removeShiny(ItemStack shiny) {
        ItemStack simplifiedStack = simplifyStack(shiny);
        return shinies.remove(simplifiedStack) == null;
    }

    public static boolean isShiny(ItemStack shiny) {
        ItemStack simplifiedStack = simplifyStack(shiny);
        return shinies.get(simplifiedStack) != null;
    }

    public static int getAffectionBoost(ItemStack shiny) {
        ItemStack simplifiedStack = simplifyStack(shiny);
        Integer result = shinies.get(simplifiedStack);
        return result == null ? 0 : result;
    }

    protected static ItemStack simplifyStack(ItemStack stack) {
        return new ItemStack(stack.getItem(), stack.getCount());
    }


}
