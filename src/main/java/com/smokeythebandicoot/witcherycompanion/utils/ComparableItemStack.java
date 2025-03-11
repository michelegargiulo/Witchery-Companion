package com.smokeythebandicoot.witcherycompanion.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ComparableItemStack {

    protected final ItemStack original;

    public ComparableItemStack(ItemStack stack) {
        this.original = stack;
        this.original.setCount(1);
        this.original.setTagCompound(null);
    }

    public ComparableItemStack(Item item, int meta) {
        this(new ItemStack(item, 1, meta));
    }

    public ItemStack getStack() {
        return this.original;
    }

    @Override
    public int hashCode() {
        if (this.original == null || this.original.isEmpty()) {
            return Objects.hash((Object) null);
        }
        return Objects.hash(this.original.getItem(), this.original.getMetadata());
    }

    @Override
    public boolean equals(Object obj) {

        // Phase 1 - check for null ComparableItemStacks
        if (obj == null) {
            return original == null;
        }
        if (!(obj instanceof ComparableItemStack)) {
            return false;
        }

        // Phase 2 - other is ComparableItemStack: compare contained stacks
        ComparableItemStack other = (ComparableItemStack) obj;

        // Check for null or empty wrapped stacks
        if (this.original == null) {
            return other.original == null;
        }
        if (this.original.isEmpty()) {
            return other.original.isEmpty();
        }

        // Only compare wrapped ItemStacks by item and meta
        return this.original.getItem() == other.getStack().getItem() &&
                this.original.getMetadata() == other.getStack().getMetadata();
    }
}