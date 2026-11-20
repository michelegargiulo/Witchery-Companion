package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestEntry;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestRequirement;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/**
 * Builder of the entry of a quest: at which levels it is offered, with what weight, and to which players. Used to change how
 * the quests of W:R are offered (setEntry), and to add them again after CovenQuests.remove or removeAll.
 * Every method returns the builder, so calls can be chained.
 */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.CovenQuestEntryBuilder")
@ZenRegister
public class CovenQuestEntryBuilder {

    /** Null if the entry could not be created (invalid id, feature disabled): every call is then a no-op */
    private final ResourceLocation id;
    private final CovenQuestRequirementList requirements;
    private int minLevel = 0;
    private int maxLevel = ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakMaxCovenSize - 1;
    private int weight = 1;
    private String notInterestedMessage = null;
    private boolean registered = false;

    public CovenQuestEntryBuilder(ResourceLocation id) {
        this.id = id;
        this.requirements = new CovenQuestRequirementList("Coven quest entry " + (id == null ? "<invalid>" : id));
    }

    private boolean ok() {
        return id != null;
    }

    @ZenMethod
    @ZenDoc(value = "Levels (witches in the coven of the player) at which the quest is offered, both included. Default: all " +
            "of them (0 to the maximum coven size - 1). Use a negative maxLevel for no upper bound")
    public CovenQuestEntryBuilder setLevels(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What a witch says when she would offer this quest at the player's level, but nothing can be offered because " +
            "of the requirements: it tells the player what is needed. A lang key (the client translates it; the witch name is " +
            "the first argument, written %s) or a text. Null or empty: the lines of W:R. When several quests are possible at " +
            "the level, one of them is chosen with the weights and its message is used")
    public CovenQuestEntryBuilder setNotInterestedMessage(String message) {
        this.notInterestedMessage = message;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Weight of the quest among the ones that can be offered: a quest with weight 2 is twice as likely as one " +
            "with weight 1. Default 1")
    public CovenQuestEntryBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    // ------------------------------------------------------------------------------------------ Requirements

    @ZenMethod
    @ZenDoc(value = "The quest has no requirement: any player can get it. Replaces the requirement of Witchery (an active " +
            "familiar) and any requirement added before")
    public CovenQuestEntryBuilder noRequirements() {
        if (ok()) requirements.clear();
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have an active familiar, of any kind. The first requirement that is added replaces " +
            "the one of Witchery (an active familiar): to keep it, add it again. All requirements must be met")
    public CovenQuestEntryBuilder requireFamiliar() {
        if (ok()) requirements.addFamiliar(null);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have an active familiar of a certain kind: the registry name of its entity, " +
            "for example minecraft:ocelot")
    public CovenQuestEntryBuilder requireFamiliar(String entityId) {
        if (ok()) requirements.addFamiliar(entityId);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to be infused with an infusion: light, earth, infernal or otherwhere (or an infusion id)")
    public CovenQuestEntryBuilder requireInfusion(String infusion) {
        if (ok()) requirements.addInfusion(infusion, 0);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to be infused with an infusion (light, earth, infernal, otherwhere) and to have at " +
            "least this much infusion power")
    public CovenQuestEntryBuilder requireInfusion(String infusion, int minPower) {
        if (ok()) requirements.addInfusion(infusion, minPower);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have at least this bottling skill")
    public CovenQuestEntryBuilder requireBottlingSkill(int minSkill) {
        if (ok()) requirements.addBottlingSkill(minSkill);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have the Werewolf curse, at least at this level")
    public CovenQuestEntryBuilder requireWerewolf(int minLevel) {
        if (ok()) requirements.addWerewolf(minLevel);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have the Vampire curse, at least at this level")
    public CovenQuestEntryBuilder requireVampire(int minLevel) {
        if (ok()) requirements.addVampire(minLevel);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have completed a quest (one of the witches of the player's coven completed it). " +
            "The id of a quest of W:R is witchery:<name>; the namespace defaults to witchery, so use the full id of custom " +
            "quests (witcherycompanion:<name>)")
    public CovenQuestEntryBuilder requirePreviousQuest(String questId) {
        if (ok()) requirements.addPreviousQuest(questId);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have visited a dimension: spirit_world, torment or mirror")
    public CovenQuestEntryBuilder requireDimensionVisited(String dimension) {
        if (ok()) requirements.addDimensionVisited(dimension);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Validates the entry and sets it. It replaces the entry that the quest had. Errors are written in the " +
            "CraftTweaker log")
    public void register() {
        if (!ok()) return;
        if (registered) {
            CraftTweakerAPI.logError("Coven quest entry " + id + ": register was already called on this builder");
            return;
        }
        registered = true;

        List<CovenQuestRequirement> list = requirements.isSet() ? requirements.get() : CovenQuestRequirement.witcheryDefault();
        CraftTweakerAPI.apply(new SetEntry(new CovenQuestEntry(id, minLevel, maxLevel, weight, list, notInterestedMessage)));
    }

    private static class SetEntry implements IAction {
        private final CovenQuestEntry entry;

        SetEntry(CovenQuestEntry entry) {
            this.entry = entry;
        }

        @Override
        public void apply() {
            List<String> problems = CovenQuestApi.setEntry(entry);
            for (String problem : problems) {
                CraftTweakerAPI.logError("Coven quest entry " + entry.getId() + " was not set: " + problem);
            }
        }

        @Override
        public String describe() {
            return "Setting the entry of coven quest " + entry.getId();
        }
    }

}
