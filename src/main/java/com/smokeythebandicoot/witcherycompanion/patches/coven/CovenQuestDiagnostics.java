package com.smokeythebandicoot.witcherycompanion.patches.coven;

import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestEntry;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestRequirement;
import com.smokeythebandicoot.witcherycompanion.api.player.PlayerExtendedDataApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.resources.CovenQuestManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Explains what the Coven Witches would do with a player right now: for every quest, how it is offered (levels, weight, who
 * can get it) and, for the quests offered at the level of the player, what each requirement says about the player. It uses the
 * same checks as the witches. Server side. Used by the /witcherycoven command to understand why a quest is, or is not, offered
 */
public final class CovenQuestDiagnostics {

    private CovenQuestDiagnostics() { }

    public static List<String> describe(EntityPlayer player) {
        List<String> lines = new ArrayList<>();
        int level = PlayerExtendedDataApi.getCovenSize(player);
        lines.add(player.getName() + ": coven size " + level + " (this is the level of the quest), maximum size "
                + CovenSizeCheat.getMaxSize());

        List<ResourceLocation> eligible = new ArrayList<>();
        Iterator<Map.Entry<ResourceLocation, CovenQuest>> it = CovenQuestManager.INSTANCE.getRegistry().iterator();
        while (it.hasNext()) {
            ResourceLocation id = it.next().getKey();
            CovenQuestEntry entry = CovenQuestApi.resolveEntry(id);
            if (entry == null) {
                lines.add("  " + id + ": not offered (removed)");
                continue;
            }

            boolean inRange = entry.isAvailableAtLevel(level);
            lines.add("  " + id + ": " + CovenQuestApi.describeLevels(entry.getMinLevel(), entry.getMaxLevel()) + ", weight "
                    + entry.getWeight() + (CovenQuestApi.hasExplicitEntry(id) ? "" : " (default entry)")
                    + (inRange ? "" : " -> not at this level"));
            if (!inRange) continue;

            boolean allMet = true;
            if (entry.getRequirements().isEmpty()) {
                lines.add("      no requirements");
            }
            for (CovenQuestRequirement requirement : entry.getRequirements()) {
                CovenQuestRequirement.Result result = requirement.evaluate(player);
                allMet &= result.isMet();
                lines.add("      " + (result.isMet() ? "OK  " : "NO  ") + requirement + " | " + result.getDetail());
            }
            if (allMet) eligible.add(id);
        }

        lines.add(eligible.isEmpty() ? "A witch would not offer any quest to " + player.getName()
                : "A witch could offer: " + eligible);
        return lines;
    }

}
