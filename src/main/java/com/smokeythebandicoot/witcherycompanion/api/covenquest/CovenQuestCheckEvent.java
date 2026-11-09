package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A player right-clicked a Coven Witch again after accepting a quest, with an item that counts for the quest (for quests that
 * work with items: the ones of W:R, and custom ITEM quests), or with anything (custom PROGRESS quests), and the witch has
 * to tell whether the task is done. Posted on the Forge event bus, server side, for every quest (the ones of W:R too), after the
 * handler of the quest (custom quests only) ran. Listeners answer through the context (see CovenQuestContext): if nobody does,
 * items are taken until the witch has all of them, like in W:R (PROGRESS quests are not finished).
 */
public class CovenQuestCheckEvent extends Event {

    private final CovenQuestContext context;

    public CovenQuestCheckEvent(CovenQuestContext context) {
        this.context = context;
    }

    public CovenQuestContext getContext() { return context; }

}
