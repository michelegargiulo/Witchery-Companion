package com.smokeythebandicoot.witcherycompanion.patches.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 Patches:
 [Bugfix] Fix WitcheryPriIncUsr NBT tag persisting after item pickup
 */
public class CommonEventsPatch {

    private static final String targetNbt = "WitcheryPriIncUsr";
    public static CommonEventsPatch INSTANCE;

    private CommonEventsPatch() { }

    static {
        INSTANCE = new CommonEventsPatch();
    }

    // NOTE: this event is subscribed only if ritePriorIncarnation_fixNbtNotRemoved is enabled in config
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerContainerOpenEvent(PlayerContainerEvent.Open event) {

        //if (event.getEntityPlayer().world.isRemote) return;
        Container container = event.getContainer();

        for (int i = 0; i < container.inventoryItemStacks.size(); i++) {
            ItemStack stack = container.getInventory().get(i);
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null && nbt.hasKey(targetNbt)) {
                nbt.removeTag(targetNbt);
                stack.setTagCompound(nbt.isEmpty() ? null : nbt);
                container.putStackInSlot(i, stack);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerPickUpItemEvent(EntityItemPickupEvent event) {
        EntityItem item = event.getItem();
        ItemStack pickupStack = item.getItem();
        if (pickupStack.getTagCompound() != null) {
            pickupStack.removeSubCompound("WitcheryPriIncUsr");
            if (pickupStack.getTagCompound().isEmpty()) {
                pickupStack.setTagCompound(null);
            }
        }
        item.setItem(pickupStack);
    }

}
