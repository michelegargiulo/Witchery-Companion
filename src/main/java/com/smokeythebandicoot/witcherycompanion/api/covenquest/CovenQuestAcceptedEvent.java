package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A player accepted a quest of a Coven Witch (second right-click on the witch). Posted on the Forge event bus, server side, for every quest (the ones of W:R too) right after the quest is activated.
 */
public class CovenQuestAcceptedEvent extends Event {

    private final CovenQuestContext context;

    public CovenQuestAcceptedEvent(CovenQuestContext context) {
        this.context = context;
    }

    public CovenQuestContext getContext() { return context; }

}
