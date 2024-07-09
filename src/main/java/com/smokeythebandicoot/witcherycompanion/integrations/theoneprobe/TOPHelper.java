package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.util.text.TextFormatting;

public class TOPHelper {

    public static IProbeInfo addText(IProbeInfo probeInfo, String prefix, String text, TextFormatting formatting) {
        return probeInfo.text(formatting + prefix + ": " + TextStyleClass.INFO + text);
    }

    public static IProbeInfo textPrefixed(IProbeInfo probeInfo, String prefix, String text) {
        return textPrefixed(probeInfo, prefix, text, TextStyleClass.LABEL);
    }

    static IProbeInfo textPrefixed(IProbeInfo probeInfo, String prefix, String text, TextStyleClass styleClass) {
        return probeInfo.text(styleClass + prefix + ": " + TextStyleClass.INFO + text);
    }
}
