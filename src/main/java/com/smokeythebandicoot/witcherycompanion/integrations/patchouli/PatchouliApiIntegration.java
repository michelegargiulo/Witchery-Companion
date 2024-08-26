package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.bookcomponents.ColorableImage;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.template.BookTemplate;

import java.util.Map;

public class PatchouliApiIntegration {

    private PatchouliApiIntegration() { }

    public static void registerCustomComponents() {
        BookTemplate.registerComponent("colored_image", ColorableImage.class);
    }

    // Called from Proxy
    public static void registerFlags() {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.instance;

        api.setConfigFlag(WitcheryCompanion.MODID + ":brewing/expertise",
                ModConfig.IntegrationConfigurations.PatchouliIntegration.Flags.brewing_enableExpertiseExtension);

        api.setConfigFlag(WitcheryCompanion.MODID + ":brewing/rituals",
                ModConfig.IntegrationConfigurations.PatchouliIntegration.Flags.brewing_enableRitualsExtension);
    }

    public static void updateFlag(String flag, boolean value) {
        PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.MODID + ":" + flag, value);
        PatchouliAPI.instance.reloadBookContents();
    }

    public static void updateFlags(Map<String, Boolean> flags) {
        for (String flag : flags.keySet()) {
            PatchouliAPI.instance.setConfigFlag(flag, flags.get(flag));
        }
        PatchouliAPI.instance.reloadBookContents();
    }

}
