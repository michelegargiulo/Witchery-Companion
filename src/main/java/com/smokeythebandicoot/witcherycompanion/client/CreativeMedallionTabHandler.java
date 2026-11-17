package com.smokeythebandicoot.witcherycompanion.client;

import com.smokeythebandicoot.witcherycompanion.client.gui.CovenSizeMedallionEntry;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.client.gui.medallion.CreativeMedallionEntry;
import net.msrandom.witchery.client.gui.medallion.GuiCreativeMedallion;

import java.util.List;

/**
 * Adds the Coven tab to the Creative Medallion when the screen is opened (the list of tabs of W:R is a mutable list that is
 * read when the screen builds its buttons), and removes it if the config says so. Client side only
 */
public class CreativeMedallionTabHandler {

    private static CovenSizeMedallionEntry covenTab;

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if (!(event.getGui() instanceof GuiCreativeMedallion)) return;

        if (covenTab == null) covenTab = new CovenSizeMedallionEntry();
        List<CreativeMedallionEntry> tabs = GuiCreativeMedallion.TABS;

        if (ModConfig.PatchesConfiguration.ItemTweaks.creativeMedallion_tweakCovenSizeTab) {
            if (!tabs.contains(covenTab)) tabs.add(covenTab);
            CovenSizeMedallionEntry.markOutdated();
        } else {
            tabs.remove(covenTab);
        }
    }

}
