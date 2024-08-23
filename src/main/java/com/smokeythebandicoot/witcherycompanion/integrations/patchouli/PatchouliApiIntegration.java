package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliApiIntegration {

    private PatchouliApiIntegration() { }

    /*
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            api.reloadBookContents();
            Utils.logChat("Reloaded book contents");
        }
    }
    */

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
    }

}
