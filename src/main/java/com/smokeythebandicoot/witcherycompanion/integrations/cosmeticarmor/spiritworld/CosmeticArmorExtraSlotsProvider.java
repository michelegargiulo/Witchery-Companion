package com.smokeythebandicoot.witcherycompanion.integrations.cosmeticarmor.spiritworld;

import com.smokeythebandicoot.witcherycompanion.api.spiritworld.IExtraSlotsProvider;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Cosmetic Armor Reworked slots (4 cosmetic armor slots and 7 for the Baubles ones). Server side only. Whether a slot
 * shows a skin instead of an item is a setting of the slot, not part of the stack, and is not touched
 */
public class CosmeticArmorExtraSlotsProvider implements IExtraSlotsProvider {

    @Override
    public String getId() {
        return Mods.COSMETIC_ARMOR;
    }

    @Override
    public int getSlots(EntityPlayer player) {
        return getStacks(player).getSlots();
    }

    @Override
    public ItemStack getStack(EntityPlayer player, int slot) {
        CAStacksBase stacks = getStacks(player);
        // Cosmetic Armor throws on a slot out of range
        return slot < 0 || slot >= stacks.getSlots() ? ItemStack.EMPTY : stacks.getStackInSlot(slot);
    }

    @Override
    public boolean setStack(EntityPlayer player, int slot, ItemStack stack) {
        CAStacksBase stacks = getStacks(player);
        if (slot < 0 || slot >= stacks.getSlots()) {
            return false;
        }

        stacks.setStackInSlot(slot, stack);
        return stack.isEmpty() || !stacks.getStackInSlot(slot).isEmpty();
    }

    private static CAStacksBase getStacks(EntityPlayer player) {
        return CosArmorAPI.getCAStacks(player.getUniqueID());
    }

}
