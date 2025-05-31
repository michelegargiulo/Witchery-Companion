package com.smokeythebandicoot.witcherycompanion.api.progress;

import net.minecraft.entity.player.EntityPlayer;


public class WitcheryProgressLockEvent extends WitcheryProgressEvent {

    public WitcheryProgressLockEvent(EntityPlayer player, String progressKey, String activityTrigger) {
        super(player, progressKey, activityTrigger);
    }

}
