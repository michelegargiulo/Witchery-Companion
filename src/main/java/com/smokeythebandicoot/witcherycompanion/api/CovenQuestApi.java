package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestEntry;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestPicker;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestRequirement;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CustomCovenQuest;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CustomCovenQuestDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.registry.WitcheryRegistry;
import net.msrandom.witchery.resources.CovenQuestManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Registry of how Coven Witches offer their quests.
 * <ul>
 *     <li><b>Entries</b> (CovenQuestEntry) say at which levels (coven sizes) a quest is offered, how likely it is among
 *     the others, and what the player must meet. Any quest that W:R has loaded (its own JSON quests, or custom quests) can
 *     have an entry. A quest without an entry is offered like in Witchery: at every level, weight 1, to players with an
 *     active familiar. To change the quests of W:R, replace their entries; to remove them use removeEntry/removeAllEntries
 *     (and add them again with setEntry, with the weights and requirements wanted)</li>
 *     <li><b>Custom quests</b> (CustomCovenQuestDefinition) add quests with code. When W:R's CovenQuestManager reloads its
 *     data, they are added to its registry (see CovenQuestManagerMixin), so they are saved by id by the coven of the
 *     player like the quests of W:R.</li>
 * </ul>
 * Everything must be registered before the world loads (or before the data reload).
 */
public class CovenQuestApi {

    private static final Map<ResourceLocation, CustomCovenQuestDefinition> CUSTOM_QUESTS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, CovenQuestEntry> ENTRIES = new LinkedHashMap<>();
    private static final Set<ResourceLocation> REMOVED = new HashSet<>();
    private static boolean defaultEntriesDisabled = false;

    private CovenQuestApi() { }

    /**
     * Registers a custom quest, and the entry (levels, weight, requirements) in its definition. A custom quest with the
     * same id is replaced.
     * @return the list of problems found. If not empty, the quest has NOT been registered
     */
    public static List<String> registerCustomQuest(CustomCovenQuestDefinition definition) {
        List<String> problems = definition.validate();
        if (!problems.isEmpty()) return problems;

        if (CUSTOM_QUESTS.put(definition.getId(), definition) != null) {
            WitcheryCompanion.logger.warn("Custom Coven Quest {} was registered twice: the previous definition is replaced", definition.getId());
        }
        REMOVED.remove(definition.getId());
        ENTRIES.put(definition.getId(), definition.toEntry());
        WitcheryCompanion.logger.info("Registered custom coven quest {} ({}, mode {}, requirements {})", definition.getId(),
                describeLevels(definition.getMinLevel(), definition.getMaxLevel()), definition.getMode(), definition.getRequirements());
        return problems;
    }

    /**
     * Sets the entry of a quest, replacing the one it had (the default one, or one set before).
     * @return the list of problems found. If not empty, the entry has NOT been set
     */
    public static List<String> setEntry(CovenQuestEntry entry) {
        List<String> problems = entry.validate();
        if (!problems.isEmpty()) return problems;

        REMOVED.remove(entry.getId());
        ENTRIES.put(entry.getId(), entry);
        return problems;
    }

    /** Quest is no longer offered (unless it gets a new entry with setEntry) */
    public static void removeEntry(ResourceLocation id) {
        ENTRIES.remove(id);
        REMOVED.add(id);
    }

    /** No quest is offered, except the ones that get an entry (setEntry) or are registered (registerCustomQuest) after this call */
    public static void removeAllEntries() {
        ENTRIES.clear();
        REMOVED.clear();
        defaultEntriesDisabled = true;
    }

    /** Forgets everything: custom quests, entries, and removals. The quests are offered like in Witchery */
    public static void clear() {
        CUSTOM_QUESTS.clear();
        ENTRIES.clear();
        REMOVED.clear();
        defaultEntriesDisabled = false;
    }

    public static boolean isEmpty() {
        return CUSTOM_QUESTS.isEmpty() && ENTRIES.isEmpty() && REMOVED.isEmpty() && !defaultEntriesDisabled;
    }

    public static Collection<CustomCovenQuestDefinition> getCustomQuests() {
        return Collections.unmodifiableCollection(CUSTOM_QUESTS.values());
    }

    /** The entry that decides how a quest is offered, or null if the quest is not offered at all */
    @Nullable
    public static CovenQuestEntry resolveEntry(ResourceLocation id) {
        CovenQuestEntry entry = ENTRIES.get(id);
        if (entry != null) return entry;
        if (defaultEntriesDisabled || REMOVED.contains(id)) return null;
        return CovenQuestEntry.witcheryDefault(id);
    }

    /**
     * Chooses the quest that a witch offers to a player: among the quests of W:R's registry, the ones that are offered at
     * the level (witches in the coven of the player), whose requirements the player meets, with a random weighted choice.
     * @return the id of the quest, or null if there is no quest that can be offered to the player
     */
    @Nullable
    public static ResourceLocation pickQuest(EntityPlayer player, int level, Random random) {
        WitcheryRegistry<ResourceLocation, CovenQuest> registry = CovenQuestManager.INSTANCE.getRegistry();

        List<ResourceLocation> candidates = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        Iterator<Map.Entry<ResourceLocation, CovenQuest>> it = registry.iterator();
        while (it.hasNext()) {
            ResourceLocation id = it.next().getKey();
            CovenQuestEntry entry = resolveEntry(id);
            if (entry == null || !entry.isAvailableAtLevel(level)) continue;
            if (!meetsRequirements(entry, player)) continue;
            candidates.add(id);
            weights.add(entry.getWeight());
        }

        int[] weightArray = new int[weights.size()];
        for (int i = 0; i < weightArray.length; i++) weightArray[i] = weights.get(i);
        int index = CovenQuestPicker.pick(weightArray, weightArray.length, random);
        return index < 0 ? null : candidates.get(index);
    }

    /** For the logs and the diagnostic command: "levels 2 to 4", "levels 0 and above" */
    public static String describeLevels(int minLevel, int maxLevel) {
        return maxLevel < 0 ? "levels " + minLevel + " and above" : "levels " + minLevel + " to " + maxLevel;
    }

    private static boolean meetsRequirements(CovenQuestEntry entry, EntityPlayer player) {
        for (CovenQuestRequirement requirement : entry.getRequirements()) {
            if (!requirement.isMet(player)) return false;
        }
        return true;
    }

    /** True if the quest has an entry set by a script or an addon (and not the default one of Witchery) */
    public static boolean hasExplicitEntry(ResourceLocation id) {
        return ENTRIES.containsKey(id);
    }

    /** True if the quest was removed (removeEntry) and did not get a new entry */
    public static boolean isRemoved(ResourceLocation id) {
        return REMOVED.contains(id);
    }

    /**
     * What a witch says when she has nothing to offer to a player. Among the quests that she would offer at the level of the
     * player (they are all rejected because of their requirements), one is chosen with the same weighted choice that picks the
     * quests, and its message is returned: it tells the player what that quest needs.
     * @return the message, or null if the chosen quest has none (or there is no such quest): the witch then says the lines of W:R
     */
    @Nullable
    public static String findNotInterestedMessage(int level, Random random) {
        WitcheryRegistry<ResourceLocation, CovenQuest> registry = CovenQuestManager.INSTANCE.getRegistry();

        List<CovenQuestEntry> candidates = new ArrayList<>();
        Iterator<Map.Entry<ResourceLocation, CovenQuest>> it = registry.iterator();
        while (it.hasNext()) {
            CovenQuestEntry entry = resolveEntry(it.next().getKey());
            if (entry != null && entry.isAvailableAtLevel(level)) candidates.add(entry);
        }

        int[] weights = new int[candidates.size()];
        for (int i = 0; i < weights.length; i++) weights[i] = candidates.get(i).getWeight();
        int index = CovenQuestPicker.pick(weights, weights.length, random);
        if (index < 0) return null;

        CovenQuestEntry chosen = candidates.get(index);
        return chosen.hasNotInterestedMessage() ? chosen.getNotInterestedMessage() : null;
    }

    /**
     * Adds the custom quests to the registry of W:R. Called every time W:R reloads the quests (it creates a new registry each
     * time). Also reports the entries that refer to quests that do not exist
     */
    public static void injectCustomQuests(WitcheryRegistry<ResourceLocation, CovenQuest> registry) {
        for (CustomCovenQuestDefinition definition : CUSTOM_QUESTS.values()) {
            if (registry.contains(definition.getId())) {
                WitcheryCompanion.logger.warn("Custom Coven Quest {} replaces a quest of W:R with the same id", definition.getId());
            }
            registry.set(definition.getId(), new CustomCovenQuest(definition));
        }

        for (ResourceLocation id : ENTRIES.keySet()) {
            if (!registry.contains(id)) {
                WitcheryCompanion.logger.warn("Coven Quest entry {} does not match any quest: it is ignored", id);
            }
        }
        if (!CUSTOM_QUESTS.isEmpty()) {
            WitcheryCompanion.logger.info("Added {} custom coven witch quests", CUSTOM_QUESTS.size());
        }
    }

}
