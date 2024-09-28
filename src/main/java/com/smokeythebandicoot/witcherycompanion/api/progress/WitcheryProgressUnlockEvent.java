package com.smokeythebandicoot.witcherycompanion.api.progress;

import net.minecraft.entity.player.EntityPlayer;


public class WitcheryProgressUnlockEvent extends WitcheryProgressEvent {

    public WitcheryProgressUnlockEvent(EntityPlayer player, String progressKey, String activityTrigger) {
        super(player, progressKey, activityTrigger);
    }
}
