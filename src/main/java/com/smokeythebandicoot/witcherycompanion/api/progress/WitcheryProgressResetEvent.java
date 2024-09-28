package com.smokeythebandicoot.witcherycompanion.api.progress;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Set;

/**
 * This event is posted on the MinecraftForge.EVENT_BUS when a player's progress has been reset
 */
public class WitcheryProgressResetEvent extends Event {

    public final EntityPlayer player;
    public final Set<String> progressKeys;

    public WitcheryProgressResetEvent(EntityPlayer player, Set<String> progressKeys) {
        this.player = player;
        this.progressKeys = progressKeys;
    }
}
