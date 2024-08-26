package com.smokeythebandicoot.witcherycompanion.proxy;

import com.smokeythebandicoot.witcherycompanion.api.capability.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.capability.WitcheryProgress;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {

    private static IWitcheryProgress localWitcheryProgress;

    public static void updateLocalWitcheryProgress(IWitcheryProgress progress) {
        if (localWitcheryProgress == null) {
            localWitcheryProgress = new WitcheryProgress();
            PatchouliAPI.instance.reloadBookContents();
        }
        if (progress != null && !progress.getUnlockedProgress().equals(localWitcheryProgress.getUnlockedProgress())) {
            localWitcheryProgress.setUnlockedProgress(progress.getUnlockedProgress());
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

}
