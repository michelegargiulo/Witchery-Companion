package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A player right-clicked a Coven Witch again after accepting a CUSTOM quest, and the witch has to tell whether the task is done. Posted on the Forge event bus, server side, after the handler of the quest (if any) ran. Listeners answer through the context (see CovenQuestContext). Quests of W:R are checked by W:R and do not post this event.
 */
public class CovenQuestCheckEvent extends Event {

    private final CovenQuestContext context;

    public CovenQuestCheckEvent(CovenQuestContext context) {
        this.context = context;
    }

    public CovenQuestContext getContext() { return context; }

}
