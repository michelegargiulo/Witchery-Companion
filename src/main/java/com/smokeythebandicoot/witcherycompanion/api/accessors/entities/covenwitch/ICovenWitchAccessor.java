package com.smokeythebandicoot.witcherycompanion.api.accessors.entities.covenwitch;

import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;

import javax.annotation.Nullable;
import java.util.UUID;

/** Access to the private quest state and talk methods of EntityCovenWitch (see EntityCovenWitchMixin) */
public interface ICovenWitchAccessor {

    @Nullable CovenQuest witcherycompanion$accessor$getQuest();

    void witcherycompanion$accessor$setQuest(@Nullable CovenQuest quest);

    @Nullable UUID witcherycompanion$accessor$getQuestOfferedTo();

    void witcherycompanion$accessor$setQuestOfferedTo(@Nullable UUID player);

    boolean witcherycompanion$accessor$isQuestAccepted();

    void witcherycompanion$accessor$setQuestAccepted(boolean accepted);

    /** Items still needed for the current quest (0 if none) */
    int witcherycompanion$accessor$getQuestItemsNeeded();

    void witcherycompanion$accessor$setQuestItemsNeeded(int items);

    /** Says the line "entity.witchery.coven_witch.say.&lt;key&gt;" of the lang files (it only gets the name of the witch) */
    void witcherycompanion$accessor$say(String key, EntityPlayer player);

    /** Says the line "entity.&lt;namespace&gt;.coven_witch.quest.&lt;path&gt;" of the lang files */
    void witcherycompanion$accessor$talkQuest(String namespace, String path, EntityPlayer player);

    void witcherycompanion$accessor$playWitchTalk();

    boolean witcherycompanion$accessor$isCovenFull(EntityPlayer player);

    /** W:R: adds the witch to the coven of the player and removes it from the world. False if the witch was already in the coven */
    boolean witcherycompanion$accessor$addToPlayerCoven(EntityPlayer player);

}
