package com.smokeythebandicoot.witcherycompanion.client.gui;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.network.CompanionNetworkChannel;
import com.smokeythebandicoot.witcherycompanion.network.PacketCovenSizeRequest;
import com.smokeythebandicoot.witcherycompanion.patches.coven.CovenSizeCheat;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;
import net.msrandom.witchery.client.gui.medallion.CreativeMedallionEntry;
import net.msrandom.witchery.client.gui.medallion.GuiCreativeMedallion;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;

/**
 * "Coven" tab of the Creative Medallion: a numeric up-down to set the number of witches in the coven of the player, ignoring
 * the requirements of the quests. The coven is not synced to the client, so the size shown is the one that the server answers
 * with (PacketCovenSizeResponse): it is asked for when the screen is opened, and after every change.
 * (W:R has a CovenMedallionEntry, but it is empty and not in the list of tabs)
 */
public class CovenSizeMedallionEntry extends CreativeMedallionEntry {

    /** Size of the coven, or -1 if the server did not answer yet */
    private static int size = -1;
    private static int max = ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakMaxCovenSize;

    /** True if the size must be asked to the server again (set when the medallion is opened) */
    private static boolean outdated = true;

    private GuiButton minusButton;
    private GuiButton plusButton;

    public CovenSizeMedallionEntry() {
        // The name of the tab is the lang key gui.medallion.button.coven, which W:R already has
        super("coven");
    }

    /** Answer of the server */
    public static void update(int newSize, int newMax) {
        size = newSize;
        max = newMax;
    }

    /** The next time the tab is shown, the size is asked to the server again */
    public static void markOutdated() {
        outdated = true;
    }

    @Override
    public void addButtons(int firstButtonId, GuiCreativeMedallion gui, int left, int top, Function1<? super GuiButton, Unit> add) {
        minusButton = new GuiButton(firstButtonId, left + 12, top + 36, 20, 20, "-");
        plusButton = new GuiButton(firstButtonId + 1, left + 131, top + 36, 20, 20, "+");
        add.invoke(minusButton);
        add.invoke(plusButton);
        updateButtons();

        if (outdated) {
            outdated = false;
            CompanionNetworkChannel.NETWORK_CHANNEL.sendToServer(new PacketCovenSizeRequest.Message(findMedallionHand(), CovenSizeCheat.QUERY));
        }
    }

    @Override
    public void draw(GuiCreativeMedallion gui, int mouseX, int mouseY, int left, int top) {
        FontRenderer font = gui.mc.fontRenderer;
        font.drawString(I18n.format("gui.witcherycompanion.medallion.coven.title"), left + 12, top + 12, 0);

        String value = size < 0 ? "?" : size + " / " + max;
        int center = left + 81;
        font.drawString(value, center - font.getStringWidth(value) / 2, top + 42, 0);

        font.drawSplitString(I18n.format("gui.witcherycompanion.medallion.coven.hint"), left + 12, top + 68, 139, 0);
        updateButtons();
    }

    @Override
    public void handleAction(GuiCreativeMedallion gui, GuiButton button, EnumHand hand) {
        if (size < 0) return;

        int target = size;
        if (button == minusButton) target--;
        else if (button == plusButton) target++;
        else return;

        target = Math.max(0, Math.min(max, target));
        if (target == size) return;

        // Shown right away: the answer of the server confirms (or corrects) it
        size = target;
        displayChangeEffect();
        CompanionNetworkChannel.NETWORK_CHANNEL.sendToServer(new PacketCovenSizeRequest.Message(hand, target));
    }

    private void updateButtons() {
        if (minusButton != null) minusButton.enabled = size > 0;
        if (plusButton != null) plusButton.enabled = size >= 0 && size < max;
    }

    /** The hand that holds the medallion: the screen is opened by right-clicking with it */
    private static EnumHand findMedallionHand() {
        boolean inMainHand = Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == WitcheryGeneralItems.CREATIVE_MEDALLION;
        return inMainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

}
