package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TinkersToolsHandler {

    /** Low priority, so that the Tinkers parts used by the tools have already been registered **/
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        TinkersTools.registerItems(event.getRegistry());
    }
}
