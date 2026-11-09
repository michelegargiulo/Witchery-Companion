package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Definition of a custom quest of the Coven Witches, to register with CovenQuestApi.registerCustomQuest.
 * <ul>
 *     <li>ITEM quests work like the ones of W:R: the player hands items to the witch. The items that count are the ones
 *     accepted by the matcher (if there is none, any item). The witch needs "itemsNeeded" of them, and the handler (or an
 *     event listener) can change what happens for each check</li>
 *     <li>PROGRESS quests are checked every time the player right-clicks the witch after accepting, with any item or with
 *     an empty hand: the handler (or an event listener) decides whether the task is done</li>
 * </ul>
 * The texts are optional: the witch says them instead of the ones in the lang files. Placeholders: {remaining} (items
 * still needed) and {player}.
 * The level range, weight and requirements (see CovenQuestEntry) default to the ones of Witchery.
 */
public class CustomCovenQuestDefinition {

    public enum Mode { ITEM, PROGRESS }

    private final ResourceLocation id;
    private Mode mode = Mode.ITEM;
    private int itemsNeeded = 1;
    @Nullable private Predicate<ItemStack> matcher;
    @Nullable private CovenQuestHandler handler;

    @Nullable private String offerText;
    @Nullable private String acceptText;
    @Nullable private String notFinishedText;
    @Nullable private String progressText;
    @Nullable private String completeText;

    private int minLevel = 0;
    private int maxLevel = -1;
    private int weight = 1;
    private List<CovenQuestRequirement> requirements = CovenQuestRequirement.witcheryDefault();

    public CustomCovenQuestDefinition(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() { return id; }
    public Mode getMode() { return mode; }
    public int getItemsNeeded() { return itemsNeeded; }
    @Nullable public Predicate<ItemStack> getMatcher() { return matcher; }
    @Nullable public CovenQuestHandler getHandler() { return handler; }
    @Nullable public String getOfferText() { return offerText; }
    @Nullable public String getAcceptText() { return acceptText; }
    @Nullable public String getNotFinishedText() { return notFinishedText; }
    @Nullable public String getProgressText() { return progressText; }
    @Nullable public String getCompleteText() { return completeText; }
    public int getMinLevel() { return minLevel; }
    public int getMaxLevel() { return maxLevel; }
    public int getWeight() { return weight; }
    public List<CovenQuestRequirement> getRequirements() { return requirements; }

    public CustomCovenQuestDefinition setMode(Mode mode) { this.mode = mode; return this; }
    public CustomCovenQuestDefinition setItemsNeeded(int itemsNeeded) { this.itemsNeeded = itemsNeeded; return this; }
    public CustomCovenQuestDefinition setMatcher(@Nullable Predicate<ItemStack> matcher) { this.matcher = matcher; return this; }
    public CustomCovenQuestDefinition setHandler(@Nullable CovenQuestHandler handler) { this.handler = handler; return this; }
    public CustomCovenQuestDefinition setOfferText(@Nullable String text) { this.offerText = text; return this; }
    public CustomCovenQuestDefinition setAcceptText(@Nullable String text) { this.acceptText = text; return this; }
    public CustomCovenQuestDefinition setNotFinishedText(@Nullable String text) { this.notFinishedText = text; return this; }
    public CustomCovenQuestDefinition setProgressText(@Nullable String text) { this.progressText = text; return this; }
    public CustomCovenQuestDefinition setCompleteText(@Nullable String text) { this.completeText = text; return this; }

    /** Levels at which the quest is offered. maxLevel below 0 = no upper bound */
    public CustomCovenQuestDefinition setLevels(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        return this;
    }

    public CustomCovenQuestDefinition setWeight(int weight) { this.weight = weight; return this; }

    /** Replaces the requirements. An empty list means that the quest has no requirement */
    public CustomCovenQuestDefinition setRequirements(List<CovenQuestRequirement> requirements) {
        this.requirements = new ArrayList<>(requirements);
        return this;
    }

    /** The entry (levels, weight, requirements) of this quest */
    public CovenQuestEntry toEntry() {
        return new CovenQuestEntry(id, minLevel, maxLevel, weight, requirements);
    }

    /** Problems of this definition (empty if it is valid) */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        if (id == null) problems.add("quest without id");
        if (mode == null) problems.add("quest without mode");
        if (itemsNeeded < 1) problems.add("items needed must be at least 1");
        problems.addAll(toEntry().validate());
        return problems;
    }

}
