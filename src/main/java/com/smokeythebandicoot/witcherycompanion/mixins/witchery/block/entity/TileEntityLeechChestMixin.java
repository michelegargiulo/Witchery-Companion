package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.msrandom.witchery.block.entity.TileEntityLeechChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@Mixin(TileEntityLeechChest.class)
public abstract class TileEntityLeechChestMixin implements IItemHandler {

    @Shadow(remap = false)
    public abstract int getSizeInventory();

    @Shadow(remap = false)
    private NonNullList<ItemStack> chestContents;

    @Shadow(remap = false)
    public abstract int getInventoryStackLimit();




    @Override
    public int getSlots() {
        return getSizeInventory();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.chestContents.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!simulate) {
            int remaining = stack.getCount();
            ItemStack toInsert = stack.copy();

            // For each pre-existing slot, try to insert in the first available slots
            for (int i = 0; i < Math.min(this.chestContents.size(), getSizeInventory()); i++) {
                ItemStack current = this.chestContents.get(i);
                if (ItemStack.areItemStacksEqual(current, stack)) {

                    // Maximum insertable items is max slot limit - the current amount
                    int insertable = getSlotLimit(i) - current.getCount();
                    if (insertable > 0) {

                        // Get how many items have been inserted
                        int inserted = Math.min(remaining, insertable);
                        ItemStack newStack = current.copy();
                        newStack.setCount(newStack.getCount() + inserted);
                        this.chestContents.set(i, newStack);

                        // Compute remainder, and if zero, we are done
                        remaining -= inserted;
                        if (remaining <= 0) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            // If current slots have not satisfied the insertion, but there are empty slots
            // available, proceed to insert in new slots
            while (remaining > 0 && this.chestContents.size() < getSizeInventory()) {
                int inserted = Math.min(getSlotLimit(this.chestContents.size()), remaining);
                toInsert.setCount(inserted);
                this.chestContents.add(this.chestContents.size(), toInsert);
                remaining -= inserted;
            }

            if (remaining == 0) {
                return ItemStack.EMPTY;
            }

            toInsert.setCount(remaining);
            return toInsert;
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!this.chestContents.get(slot).isEmpty()) {
            ItemStack present = this.chestContents.get(slot);
            int extractedAmount = Math.min(amount, Math.min(present.getCount(), present.getMaxStackSize()));
            int leftover = present.getCount() - extractedAmount;
            if (leftover <= 0) {
                this.chestContents.set(slot, ItemStack.EMPTY);
            } else {
                ItemStack leftoverStack = present.copy();
                leftoverStack.setCount(leftover);
                this.chestContents.set(slot, leftoverStack);
            }
            ItemStack extracted = present.copy();
            extracted.setCount(extractedAmount);
            return extracted;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInventoryStackLimit();
    }

}
