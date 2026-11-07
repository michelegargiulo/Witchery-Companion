package com.smokeythebandicoot.witcherycompanion.integrations.baubles.client;

import baubles.client.gui.GuiBaublesButton;
import net.minecraft.client.gui.GuiButton;

import java.util.Iterator;
import java.util.List;

/** Client side helper. Only to be called when Baubles is loaded **/
public class BaublesButtonHelper {

    /** Removes every button except the ones that Baubles adds to open and close its inventory **/
    public static void removeAllButBaublesButtons(List<GuiButton> buttons) {
        Iterator<GuiButton> iterator = buttons.iterator();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof GuiBaublesButton)) {
                iterator.remove();
            }
        }
    }

}
