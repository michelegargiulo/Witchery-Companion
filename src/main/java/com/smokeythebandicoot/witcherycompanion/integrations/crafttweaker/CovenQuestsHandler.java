package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Entry point of the Coven Witch quests in ZenScript:
 * <ul>
 *     <li>CovenQuests.create("id") starts the definition of a custom quest</li>
 *     <li>CovenQuests.setEntry("witchery:id") changes at which levels, with which weight and to whom a quest is offered
 *     (the ones of W:R, or custom ones)</li>
 *     <li>CovenQuests.remove("witchery:id") and CovenQuests.removeAll() stop offering quests</li>
 * </ul>
 * Without scripts the quests are offered like in Witchery: all of them, at every level, with the same chance, to players that
 * have an active familiar. Statements are executed in order, so removeAll() comes before the setEntry calls that follow it.
 */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.CovenQuests")
@ZenRegister
public class CovenQuestsHandler {

    @ZenMethod
    @ZenDoc(value = "Starts the definition of a custom quest. The id is 'namespace:name' (the namespace defaults to " +
            "witcherycompanion; only a-z 0-9 _ . - are allowed). The offer text of the quest is the lang key " +
            "entity.<namespace>.coven_witch.quest.<name> (or use setOfferText). Nothing is added until register() is called on the " +
            "builder. Defining a quest with an existing custom id replaces it")
    public static CovenQuestBuilder create(String id) {
        if (!enabled("Quest '" + id + "' will be ignored")) return new CovenQuestBuilder(null);
        ResourceLocation location = CovenQuestRequirementList.parseId(id, "witcherycompanion");
        if (location == null) {
            CraftTweakerAPI.logError("Cannot create custom coven quest: '" + id + "' is not a valid id (namespace:name, with a-z 0-9 _ . - only)");
        }
        return new CovenQuestBuilder(location);
    }

    @ZenMethod
    @ZenDoc(value = "Starts the definition of how a quest is offered: levels, weight and requirements. The quest is one of W:R " +
            "(for example witchery:get_bones; the namespace defaults to witchery) or a custom one (use its full id). The " +
            "entry replaces the one of the quest. Nothing changes until register() is called on the builder")
    public static CovenQuestEntryBuilder setEntry(String questId) {
        if (!enabled("The entry of quest '" + questId + "' will be ignored")) return new CovenQuestEntryBuilder(null);
        ResourceLocation location = CovenQuestRequirementList.parseId(questId, "witchery");
        if (location == null) {
            CraftTweakerAPI.logError("Cannot set the entry of a coven quest: '" + questId + "' is not a valid id");
        }
        return new CovenQuestEntryBuilder(location);
    }

    @ZenMethod
    @ZenDoc(value = "Stops the Coven Witches from offering a quest (one of W:R, or a custom one), until it gets a new entry " +
            "with setEntry. The namespace defaults to witchery")
    public static void remove(String questId) {
        if (!enabled("Removal of quest '" + questId + "' will be ignored")) return;
        ResourceLocation location = CovenQuestRequirementList.parseId(questId, "witchery");
        if (location == null) {
            CraftTweakerAPI.logError("Cannot remove a coven quest: '" + questId + "' is not a valid id");
            return;
        }
        CraftTweakerAPI.apply(new RemoveQuest(location));
    }

    @ZenMethod
    @ZenDoc(value = "Stops the Coven Witches from offering any quest, except the ones that get an entry (setEntry) or are " +
            "created (create(...).register()) after this call. To remove the familiar requirement of Witchery from all the " +
            "quests: removeAll(), then setEntry for every quest of W:R without requireFamiliar, with the weights you want")
    public static void removeAll() {
        if (!enabled("removeAll will be ignored")) return;
        CraftTweakerAPI.apply(new RemoveAll());
    }

    private static boolean enabled(String consequence) {
        if (ModConfig.IntegrationConfigurations.CraftTweakerIntegration.enableCovenQuests) return true;
        CraftTweakerAPI.logWarning("Coven Quests are disabled in the Witchery: Companion config. " + consequence);
        return false;
    }

    private static class RemoveQuest implements IAction {
        private final ResourceLocation id;

        RemoveQuest(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void apply() {
            CovenQuestApi.removeEntry(id);
        }

        @Override
        public String describe() {
            return "Removing coven quest " + id;
        }
    }

    private static class RemoveAll implements IAction {
        @Override
        public void apply() {
            CovenQuestApi.removeAllEntries();
        }

        @Override
        public String describe() {
            return "Removing all coven quests";
        }
    }

}
