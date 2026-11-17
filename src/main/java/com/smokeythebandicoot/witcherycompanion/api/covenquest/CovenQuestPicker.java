package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import java.util.Random;

/**
 * Pure logic of the quest selection (no Minecraft types, so that it can be unit-tested): level ranges and weighted
 * random choice.
 */
public final class CovenQuestPicker {

    private CovenQuestPicker() {}

    /** True if a quest with the given level range can be given to a player whose coven has "level" witches.
     * A maxLevel below 0 means that the range has no upper bound */
    public static boolean inLevelRange(int level, int minLevel, int maxLevel) {
        return level >= minLevel && (maxLevel < 0 || level <= maxLevel);
    }

    /**
     * Weighted random choice.
     * @param weights weight of each candidate (candidates with a weight of 0 or less are never chosen)
     * @param count how many entries of the array are candidates
     * @return the index of the chosen candidate, or -1 if there is no candidate with a positive weight
     */
    public static int pick(int[] weights, int count, Random random) {
        long total = 0;
        for (int i = 0; i < count; i++) {
            if (weights[i] > 0) total += weights[i];
        }
        if (total <= 0) return -1;

        long roll = total <= Integer.MAX_VALUE ? random.nextInt((int) total) : (long) (random.nextDouble() * total);
        for (int i = 0; i < count; i++) {
            if (weights[i] <= 0) continue;
            if (roll < weights[i]) return i;
            roll -= weights[i];
        }
        // Unreachable, but a rounding error must not hand out no quest
        for (int i = count - 1; i >= 0; i--) {
            if (weights[i] > 0) return i;
        }
        return -1;
    }

}
