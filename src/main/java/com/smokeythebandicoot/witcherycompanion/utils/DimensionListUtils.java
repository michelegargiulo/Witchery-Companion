package com.smokeythebandicoot.witcherycompanion.utils;

public class DimensionListUtils {

    /**
     * Evaluates a Dimension ID against a configured list of Dimension IDs. An empty list means the feature is
     * not in use, so nothing is ever blocked, in both modes
     * @param dimensionId the Dimension ID to evaluate
     * @param list the configured Dimension IDs
     * @param isWhitelist if false, the listed Dimensions are blocked. If true, only the listed Dimensions are
     *                    allowed, and every other Dimension is blocked
     * @return true if the Dimension is blocked
     */
    public static boolean isBlocked(int dimensionId, int[] list, boolean isWhitelist) {
        if (list == null || list.length == 0) return false;
        boolean listed = false;
        for (int id : list) {
            if (id == dimensionId) {
                listed = true;
                break;
            }
        }
        return isWhitelist != listed;
    }

}
