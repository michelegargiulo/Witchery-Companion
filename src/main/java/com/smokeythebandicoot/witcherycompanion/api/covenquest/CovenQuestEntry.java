package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * How a quest (one of W:R's quests, loaded from its data, or a custom quest) is offered by the Coven Witches:
 * <ul>
 *     <li>levels: a witch offers the quest to a player if the number of witches in the player's coven (the "level") is
 *     between minLevel and maxLevel (included). A maxLevel below 0 means no upper bound</li>
 *     <li>weight: relative chance of the quest among the ones that can be offered. A quest with weight 2 is twice as
 *     likely as one with weight 1</li>
 *     <li>requirements: what the player must meet for the quest to be offered (empty list = nothing)</li>
 * </ul>
 * The entry of Witchery is: every level (from 0 to the maximum coven size - 1: a witch does not offer quests to a player with a
 * full coven), weight 1, the player must have an active familiar.
 */
public final class CovenQuestEntry {

    private final ResourceLocation id;
    private final int minLevel;
    private final int maxLevel;
    private final int weight;
    private final List<CovenQuestRequirement> requirements;

    public CovenQuestEntry(ResourceLocation id, int minLevel, int maxLevel, int weight, List<CovenQuestRequirement> requirements) {
        this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.weight = weight;
        this.requirements = Collections.unmodifiableList(new ArrayList<>(requirements));
    }

    /** The entry that a quest has when nothing says otherwise: it behaves like in Witchery. Levels go from 0 to the maximum
     * coven size (see the config) - 1 */
    public static CovenQuestEntry witcheryDefault(ResourceLocation id) {
        int maxLevel = ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakMaxCovenSize - 1;
        return new CovenQuestEntry(id, 0, maxLevel, 1, CovenQuestRequirement.witcheryDefault());
    }

    public ResourceLocation getId() { return id; }
    public int getMinLevel() { return minLevel; }
    public int getMaxLevel() { return maxLevel; }
    public int getWeight() { return weight; }
    public List<CovenQuestRequirement> getRequirements() { return requirements; }

    public boolean isAvailableAtLevel(int level) {
        return CovenQuestPicker.inLevelRange(level, minLevel, maxLevel);
    }

    /** Problems of this entry (empty if it is valid) */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        if (id == null) problems.add("quest without id");
        if (minLevel < 0) problems.add("min level cannot be negative");
        if (maxLevel >= 0 && maxLevel < minLevel) problems.add("max level (" + maxLevel + ") is lower than min level (" + minLevel + ")");
        if (weight < 1) problems.add("weight must be at least 1");
        for (CovenQuestRequirement requirement : requirements) {
            problems.addAll(requirement.validate());
        }
        return problems;
    }

}
