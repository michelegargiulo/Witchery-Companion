package com.smokeythebandicoot.witcherycompanion.integrations.baubles.spiritworld;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.smokeythebandicoot.witcherycompanion.api.spiritworld.IExtraSlotsProvider;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Baubles slots. Mods that add slots by replacing the Baubles container (like Bring Me The Rings) are covered too,
 * since the slots are always read from the handler returned by Baubles
 */
public class BaublesExtraSlotsProvider implements IExtraSlotsProvider {

    @Override
    public String getId() {
        return Mods.BAUBLES;
    }

    @Override
    public int getSlots(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        return handler == null ? 0 : handler.getSlots();
    }

    @Override
    public ItemStack getStack(EntityPlayer player, int slot) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        return handler == null ? ItemStack.EMPTY : handler.getStackInSlot(slot);
    }

    @Override
    public boolean setStack(EntityPlayer player, int slot, ItemStack stack) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        if (handler == null) {
            return false;
        }

        if (stack.isEmpty()) {
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            return true;
        }

        // Baubles silently ignores a stack that the slot does not accept, so check it first
        if (!handler.isItemValidForSlot(slot, stack, player)) {
            return false;
        }
        handler.setStackInSlot(slot, stack);
        return !handler.getStackInSlot(slot).isEmpty();
    }

}
