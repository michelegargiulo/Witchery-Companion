package com.smokeythebandicoot.witcherycompanion.proxy;

import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {

    private static IWitcheryProgress localWitcheryProgress;

    public static void updateLocalWitcheryProgress(IWitcheryProgress progress) {
        boolean newProgress = false;
        if (localWitcheryProgress == null) {
            localWitcheryProgress = new WitcheryProgress();
            newProgress = true;
        }
        if (progress != null && !progress.getUnlockedProgress().equals(localWitcheryProgress.getUnlockedProgress())) {
            localWitcheryProgress.setUnlockedProgress(progress.getUnlockedProgress());
            newProgress = true;
        }

        if (newProgress && Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliAPI.instance.reloadBookContents();
        }
    }

    @Nonnull
    public static IWitcheryProgress getLocalWitcheryProgress() {
        if (localWitcheryProgress == null) {
            localWitcheryProgress = new WitcheryProgress();
        }
        return localWitcheryProgress;
    }

    public static void updateLocalDivinationStatus(DivinationData data) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Utils.logChat("From PROXY");
        Utils.logChat(data.writeToNBT().toString());
        player.setGameType(data.getGameType());
        /*player.setPositionAndRotation(
                data.getPosX(),
                data.getPosY(),
                data.getPosZ(),
                data.getYaw(),
                data.getPitch()
        );*/
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Non-configurable, as it is required for Patchouli integration and does nothing if Patchouli is not used
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.registerCustomComponents();
            PatchouliApiIntegration.registerCustomMacros();
        }
    }
}
