package com.smokeythebandicoot.witcherycompanion.integrations.api;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.init.items.WitcheryBrewItems;
import net.msrandom.witchery.init.items.WitcheryContractItems;

import java.util.HashMap;
import java.util.Objects;

public class InfernalImpApi {

    public static HashMap<SimpleItemStack, Integer> shinies = new HashMap<>();
    public static HashMap<Integer, ItemStack> gifts = new HashMap<>();

    static {
        shinies.put(new SimpleItemStack(Items.DIAMOND), 8);
        shinies.put(new SimpleItemStack(Items.DIAMOND_AXE), 24);
        shinies.put(new SimpleItemStack(Items.DIAMOND_HOE), 16);
        shinies.put(new SimpleItemStack(Items.DIAMOND_SWORD), 16);
        shinies.put(new SimpleItemStack(Items.DIAMOND_SHOVEL), 8);
        shinies.put(new SimpleItemStack(Items.DIAMOND_PICKAXE), 24);
        shinies.put(new SimpleItemStack(Items.EMERALD), 3);
        shinies.put(new SimpleItemStack(Items.GOLD_INGOT), 1);
        shinies.put(new SimpleItemStack(Items.NETHER_STAR), 16);
        shinies.put(new SimpleItemStack(Items.BLAZE_ROD), 1);
        shinies.put(new SimpleItemStack(Items.GHAST_TEAR), 4);
        shinies.put(new SimpleItemStack(Items.GOLDEN_AXE), 3);
        shinies.put(new SimpleItemStack(Items.GOLDEN_SWORD), 2);
        shinies.put(new SimpleItemStack(Items.GOLDEN_HOE), 2);
        shinies.put(new SimpleItemStack(Items.GOLDEN_SHOVEL), 1);
        shinies.put(new SimpleItemStack(Items.GOLDEN_PICKAXE), 3);
        shinies.put(new SimpleItemStack(Blocks.GOLD_BLOCK), 9);
        shinies.put(new SimpleItemStack(Blocks.EMERALD_BLOCK), 27);
        shinies.put(new SimpleItemStack(Blocks.DIAMOND_BLOCK), 72);
        shinies.put(new SimpleItemStack(Blocks.LAPIS_BLOCK), 7);
        shinies.put(new SimpleItemStack(Blocks.REDSTONE_BLOCK), 5);

        gifts.put(0, new ItemStack(WitcheryBrewItems.SOUL_HUNGER_BREW));
        gifts.put(1, new ItemStack(WitcheryBrewItems.SOUL_FEAR_BREW));
        gifts.put(2, new ItemStack(WitcheryBrewItems.SOUL_ANGUISH_BREW));
        gifts.put(3, new ItemStack(WitcheryContractItems.TORMENT_CONTRACT));
    }

    // ============================== SHINIES ==============================
    /** Adds an itemstack to the shiny list. The stack will give the provided affection boost.
     * Affection boost must be positive. Returns true if operation was successful */
    public static boolean addShiny(ItemStack shiny, int affectionBoost) {
        if (affectionBoost <= 0) return false;
        return shinies.put(new SimpleItemStack(shiny), affectionBoost) == null;
    }

    /** Removes an itemstack from the shiny list. The stack will no longer give an affection
     * boost. Returns false if the item was not a shiny */
    public static boolean removeShiny(ItemStack shiny) {
        SimpleItemStack simplifiedStack = new SimpleItemStack(shiny);
        return shinies.remove(simplifiedStack) == null;
    }

    /** Returns true if the given itemstack is a shiny, thus it provides an affection boost */
    public static boolean isShiny(ItemStack shiny) {
        SimpleItemStack simplifiedStack = new SimpleItemStack(shiny);
        return shinies.get(simplifiedStack) != null;
    }

    /** Returns the affection boost of the given itemstack. If the item is not a shiny, returns zero */
    public static int getAffectionBoost(ItemStack shiny) {
        SimpleItemStack simplifiedStack = new SimpleItemStack(shiny);
        Integer result = shinies.get(simplifiedStack);
        return result == null ? 0 : result;
    }

    // ============================== SHINIES ==============================

    /** Returns the gift for the given index. Returns null if no gift has been set for the index */
    public static ItemStack getGift(int secretNumber) {
        if (!gifts.containsKey(secretNumber)) return null;
        return gifts.get(secretNumber);
    }

    /** Sets the gift for the given index. If gift is null, ItemStack.EMPTY will be inserted.
     * Index must be non-negative */
    public static void setGift(ItemStack gift, int secretNumber) {
        if (secretNumber < 0) return;
        gifts.put(secretNumber, gift == null ? ItemStack.EMPTY : gift);
    }

    // ============================== UTILS ==============================
    private static class SimpleItemStack {
        public String regName;
        public int meta;
        public NBTTagCompound nbt;

        public SimpleItemStack(ItemStack stack) {
            regName = stack.getItem().getRegistryName().getNamespace() + ":" + stack.getItem().getRegistryName().getPath();
            meta = stack.getMetadata();
            nbt = stack.getTagCompound();
        }

        public SimpleItemStack(Item item) {
            regName = item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath();
            meta = 0;
            nbt = null;
        }

        public SimpleItemStack(Block block) {
            Item item = Item.getItemFromBlock(block);
            regName = item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath();
            //meta = block.get;
            meta = 0;
            nbt = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleItemStack that = (SimpleItemStack) o;
            return meta == that.meta && Objects.equals(regName, that.regName) && Objects.equals(nbt, that.nbt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regName, meta, nbt);
        }

        @Override
        public String toString() {
            return "SimpleItemStack{" +
                    "regName='" + regName + '\'' +
                    ", meta=" + meta +
                    //", nbt=" + nbt +
                    '}';
        }
    }

}
