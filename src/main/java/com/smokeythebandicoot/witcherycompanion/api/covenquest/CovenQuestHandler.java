package com.smokeythebandicoot.witcherycompanion.api.covenquest;

/**
 * Logic of a custom quest. Both methods run on the server. They receive the same context that is carried by the
 * CovenQuestAcceptedEvent and CovenQuestCheckEvent, and the events are posted too, so the same can be done by
 * subscribing to the events.
 */
public interface CovenQuestHandler {

    /** The player accepted the quest (second right-click on the witch) */
    default void onAccept(CovenQuestContext context) { }

    /** The player right-clicked the witch again after accepting the quest. See CovenQuestContext to tell the witch what to
     * do: complete the quest, count some progress, or say that the task is not finished */
    default void check(CovenQuestContext context) { }

}
