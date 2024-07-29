package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class TOPHelper {

    public static IProbeInfo addText(IProbeInfo probeInfo, String prefix, String text, TextFormatting formatting) {
        return probeInfo.text(formatting + prefix + ": " + TextStyleClass.INFO + text);
    }

    public static IProbeInfo textPrefixed(IProbeInfo probeInfo, String prefix, String text) {
        return textPrefixed(probeInfo, prefix, text, TextStyleClass.LABEL);
    }

    public static IProbeInfo textPrefixed(IProbeInfo probeInfo, String prefix, String text, TextStyleClass styleClass) {
        return probeInfo.text(styleClass + prefix + ": " + TextStyleClass.INFO + text);
    }

    public static IProbeInfo itemStacks(IProbeInfo probeInfo, List<ItemStack> stacks, int maxInRow) {
        int curRow = 0;
        IProbeInfo vertical = probeInfo.vertical();
        IProbeInfo currentRow = vertical.horizontal();

        for (ItemStack stack : stacks) {
            currentRow.item(stack);
            curRow++;
            if (curRow >= maxInRow) {
                curRow = 0;
                currentRow = vertical.horizontal();
            }
        }
        return probeInfo;
    }
}
