package com.smokeythebandicoot.witcherycompanion.api.spiritworld;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Gives {@link com.smokeythebandicoot.witcherycompanion.api.SpiritWorldTransferApi} access to inventory slots that a
 * mod adds to the player, other than the vanilla ones (for example Baubles).
 * <p>
 * When the extra slots feature is enabled, those slots are treated like the rest of the inventory: whatever is in
 * them and is not allowed to cross the Spirit World barrier is stored when the player crosses it, and given back
 * when the player comes back.
 */
public interface IExtraSlotsProvider {

    /** Unique id of the provider. It is saved with the stored items, so it must never change (the modid works) */
    String getId();

    /** Number of extra slots the player has */
    int getSlots(EntityPlayer player);

    /** The stack in the slot, never null */
    ItemStack getStack(EntityPlayer player, int slot);

    /**
     * Puts the stack in the slot, which is empty. An empty stack empties the slot and is always accepted. Returns
     * false if the slot refused the stack, in which case nothing must change
     */
    boolean setStack(EntityPlayer player, int slot, ItemStack stack);

}
