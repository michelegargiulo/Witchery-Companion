package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestContext;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestHandler;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CustomCovenQuestDefinition;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.util.IEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/**
 * Builder of a custom Coven Witch quest. Every method returns the builder, so calls can be chained.
 * A quest is either an item quest (the default: the player hands items to the witch, like in W:R) or a progress quest
 * (checked every time the player right-clicks the witch after accepting, even with an empty hand).
 */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.CovenQuestBuilder")
@ZenRegister
public class CovenQuestBuilder {

    /** Null if the quest could not be created (invalid id, feature disabled): every call is then a no-op */
    private final CustomCovenQuestDefinition definition;
    private final CovenQuestRequirementList requirements;
    private IEventHandler<QuestContext> onAccept = null;
    private IEventHandler<QuestContext> onCheck = null;
    private boolean registered = false;

    public CovenQuestBuilder(ResourceLocation id) {
        this.definition = id == null ? null : new CustomCovenQuestDefinition(id);
        this.requirements = new CovenQuestRequirementList("Custom coven quest " + (id == null ? "<invalid>" : id));
    }

    private boolean ok() {
        return definition != null;
    }

    private void error(String message) {
        CraftTweakerAPI.logError("Custom coven quest " + (ok() ? definition.getId() : "<invalid>") + ": " + message);
    }

    // ------------------------------------------------------------------------------------------ Texts

    @ZenMethod
    @ZenDoc(value = "What the witch says when she offers the quest (first right-click). Without it, the line " +
            "entity.<namespace>.coven_witch.quest.<name> of the lang files. Placeholders: {remaining} and {player}")
    public CovenQuestBuilder setOfferText(String text) {
        if (ok()) definition.setOfferText(text);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What the witch says when the player accepts the quest (second right-click). Without it she says nothing")
    public CovenQuestBuilder setAcceptText(String text) {
        if (ok()) definition.setAcceptText(text);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What the witch says when the task is not finished. Without it, she says the line of W:R")
    public CovenQuestBuilder setNotFinishedText(String text) {
        if (ok()) definition.setNotFinishedText(text);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What the witch says when only part of the task was done. Placeholder {remaining}: items still needed. " +
            "Without it, she says the line of W:R")
    public CovenQuestBuilder setProgressText(String text) {
        if (ok()) definition.setProgressText(text);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What the witch says when the quest is completed and she joins the coven. Without it, she says the line of W:R")
    public CovenQuestBuilder setCompleteText(String text) {
        if (ok()) definition.setCompleteText(text);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "What a witch says when she would offer this quest at the player's level, but nothing can be offered because " +
            "of the requirements: it tells the player what is needed. A lang key (the client translates it; the witch name is " +
            "the first argument, written %s) or a text. Null or empty: the lines of W:R. When several quests are possible at " +
            "the level, one of them is chosen with the weights and its message is used")
    public CovenQuestBuilder setNotInterestedMessage(String message) {
        if (ok()) definition.setNotInterestedMessage(message);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Task

    @ZenMethod
    @ZenDoc(value = "Makes this an item quest (the default): the player hands items to the witch. The items that count are " +
            "the ones that match the ingredient (items, ore dictionary, anyDamage(), only(function)...). The amount of the " +
            "ingredient is the number of items that the witch needs (<minecraft:diamond> * 3). Note that in CraftTweaker " +
            "withTag(tag) alone does not filter by NBT. Without this call, any item counts")
    public CovenQuestBuilder setItem(IIngredient ingredient) {
        if (!ok()) return this;
        if (ingredient == null) {
            error("setItem: the ingredient is null");
            return this;
        }
        definition.setMode(CustomCovenQuestDefinition.Mode.ITEM);
        // The amount is how many items the witch needs: CraftTweaker only matches a stack with at least that many items, but
        // the player can hand them in several times, so the matcher looks at the kind of item only
        definition.setMatcher(new IngredientMatcher(ingredient.amount(1)));
        definition.setItemsNeeded(Math.max(1, ingredient.getAmount()));
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Number of items that the witch needs, for item quests. Default 1")
    public CovenQuestBuilder setItemsNeeded(int items) {
        if (ok()) definition.setItemsNeeded(items);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Makes this a progress quest: the quest is checked every time the player right-clicks the witch after " +
            "accepting it, with any item or with an empty hand. setOnCheck decides whether the task is done")
    public CovenQuestBuilder setProgressQuest() {
        if (ok()) definition.setMode(CustomCovenQuestDefinition.Mode.PROGRESS);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Level and weight

    @ZenMethod
    @ZenDoc(value = "Levels (witches in the coven of the player) at which the quest is offered, both included. Default: all " +
            "of them (0 to the maximum coven size - 1). Use a negative maxLevel for no upper bound")
    public CovenQuestBuilder setLevels(int minLevel, int maxLevel) {
        if (ok()) definition.setLevels(minLevel, maxLevel);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Weight of the quest among the ones that can be offered: a quest with weight 2 is twice as likely as one " +
            "with weight 1. Default 1")
    public CovenQuestBuilder setWeight(int weight) {
        if (ok()) definition.setWeight(weight);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Requirements

    @ZenMethod
    @ZenDoc(value = "The quest has no requirement: any player can get it. Replaces the requirement of Witchery (an active " +
            "familiar) and any requirement added before")
    public CovenQuestBuilder noRequirements() {
        if (ok()) requirements.clear();
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have an active familiar, of any kind. The first requirement that is added replaces " +
            "the one of Witchery (an active familiar): to keep it, add it again. All requirements must be met")
    public CovenQuestBuilder requireFamiliar() {
        if (ok()) requirements.addFamiliar(null);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have an active familiar of a certain kind: the registry name of its entity, " +
            "for example minecraft:ocelot")
    public CovenQuestBuilder requireFamiliar(String entityId) {
        if (ok()) requirements.addFamiliar(entityId);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to be infused with an infusion: light, earth, infernal or otherwhere (or an infusion id)")
    public CovenQuestBuilder requireInfusion(String infusion) {
        if (ok()) requirements.addInfusion(infusion, 0);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to be infused with an infusion (light, earth, infernal, otherwhere) and to have at " +
            "least this much infusion power")
    public CovenQuestBuilder requireInfusion(String infusion, int minPower) {
        if (ok()) requirements.addInfusion(infusion, minPower);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have at least this bottling skill")
    public CovenQuestBuilder requireBottlingSkill(int minSkill) {
        if (ok()) requirements.addBottlingSkill(minSkill);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have the Werewolf curse, at least at this level")
    public CovenQuestBuilder requireWerewolf(int minLevel) {
        if (ok()) requirements.addWerewolf(minLevel);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have the Vampire curse, at least at this level")
    public CovenQuestBuilder requireVampire(int minLevel) {
        if (ok()) requirements.addVampire(minLevel);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have completed a quest (one of the witches of the player's coven completed it). " +
            "The id of a quest of W:R is witchery:<name>; the namespace defaults to witchery, so use the full id of custom " +
            "quests (witcherycompanion:<name>)")
    public CovenQuestBuilder requirePreviousQuest(String questId) {
        if (ok()) requirements.addPreviousQuest(questId);
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Requires the player to have visited a dimension: spirit_world, torment or mirror")
    public CovenQuestBuilder requireDimensionVisited(String dimension) {
        if (ok()) requirements.addDimensionVisited(dimension);
        return this;
    }

    // ------------------------------------------------------------------------------------------ Logic

    @ZenMethod
    @ZenDoc(value = "A function that receives a QuestContext when the player accepts the quest (second right-click). It runs " +
            "on the server, after the quest was activated")
    public CovenQuestBuilder setOnAccept(IEventHandler<QuestContext> handler) {
        this.onAccept = handler;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "A function that receives a QuestContext when the player right-clicks the witch again after accepting. " +
            "It runs on the server and answers with context.complete(), context.progress(n) or context.notFinished(). Without " +
            "an answer, item quests take the items the witch needs, and progress quests are not finished. For item quests it " +
            "only runs if the player holds an item that matches")
    public CovenQuestBuilder setOnCheck(IEventHandler<QuestContext> handler) {
        this.onCheck = handler;
        return this;
    }

    @ZenMethod
    @ZenDoc(value = "Validates the quest and adds it. Errors are written in the CraftTweaker log")
    public void register() {
        if (!ok()) return;
        if (registered) {
            error("register was already called on this builder");
            return;
        }
        registered = true;

        if (requirements.isSet()) definition.setRequirements(requirements.get());
        if (onAccept != null || onCheck != null) definition.setHandler(new ScriptHandler(onAccept, onCheck));
        CraftTweakerAPI.apply(new AddCovenQuest(definition));
    }

    // ------------------------------------------------------------------------------------------ Helpers

    private static class AddCovenQuest implements IAction {
        private final CustomCovenQuestDefinition definition;

        AddCovenQuest(CustomCovenQuestDefinition definition) {
            this.definition = definition;
        }

        @Override
        public void apply() {
            List<String> problems = CovenQuestApi.registerCustomQuest(definition);
            for (String problem : problems) {
                CraftTweakerAPI.logError("Custom coven quest " + definition.getId() + " was not added: " + problem);
            }
        }

        @Override
        public String describe() {
            return "Adding custom coven quest " + definition.getId();
        }
    }

    /** Matches the items that a CraftTweaker ingredient matches. Not a mixin: lambdas are fine */
    private static class IngredientMatcher implements java.util.function.Predicate<ItemStack> {
        private final IIngredient ingredient;

        IngredientMatcher(IIngredient ingredient) {
            this.ingredient = ingredient;
        }

        @Override
        public boolean test(ItemStack stack) {
            return ingredient.matches(CraftTweakerMC.getIItemStack(stack));
        }
    }

    /** Bridges the Java handler of the API to the ZenScript functions */
    private static class ScriptHandler implements CovenQuestHandler {
        private final IEventHandler<QuestContext> accept;
        private final IEventHandler<QuestContext> check;

        ScriptHandler(IEventHandler<QuestContext> accept, IEventHandler<QuestContext> check) {
            this.accept = accept;
            this.check = check;
        }

        @Override
        public void onAccept(CovenQuestContext context) {
            if (accept != null) accept.handle(new QuestContext(context));
        }

        @Override
        public void check(CovenQuestContext context) {
            if (check != null) check.handle(new QuestContext(context));
        }
    }

}
