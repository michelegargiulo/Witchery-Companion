package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Pairs a list of "wanted" ingredients with a pool of available objects, so that every ingredient is assigned to a
 * DIFFERENT pool entry (a pool entry can never satisfy two ingredients, and an ingredient needs exactly one entry).
 * <p>
 * This is a bipartite matching problem (solved here with augmenting paths), and not a greedy scan: a broad ingredient
 * (OreDict, wildcard metadata...) must not "steal" the only entry that a narrower ingredient can use.
 */
public final class IngredientMatcher {

    private IngredientMatcher() {}

    /**
     * @param wanted The ingredients that must all be satisfied
     * @param pool The available objects. Entries that match no ingredient are simply ignored
     * @param predicate Tells if a given pool entry satisfies a given ingredient
     * @return true if every ingredient can be assigned to its own pool entry
     */
    public static <W, T> boolean matchesAll(List<W> wanted, List<T> pool, BiPredicate<? super W, ? super T> predicate) {
        int n = wanted.size();
        int m = pool.size();
        if (n == 0) return true;
        if (n > m) return false;

        boolean[][] compatible = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                compatible[i][j] = predicate.test(wanted.get(i), pool.get(j));
            }
        }

        // owner[j] = index of the ingredient currently assigned to pool entry j, or -1
        int[] owner = new int[m];
        Arrays.fill(owner, -1);
        for (int i = 0; i < n; i++) {
            if (!assign(i, compatible, owner, new boolean[m])) return false;
        }
        return true;
    }

    private static boolean assign(int ingredient, boolean[][] compatible, int[] owner, boolean[] visited) {
        for (int j = 0; j < owner.length; j++) {
            if (!compatible[ingredient][j] || visited[j]) continue;
            visited[j] = true;
            // Free entry, or the ingredient that holds it can be moved elsewhere
            if (owner[j] < 0 || assign(owner[j], compatible, owner, visited)) {
                owner[j] = ingredient;
                return true;
            }
        }
        return false;
    }

}
