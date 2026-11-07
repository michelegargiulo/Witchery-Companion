package com.smokeythebandicoot.witcherycompanion.utils.blood;

import java.util.Locale;

/**
 * Pure logic of the blood command: turns the value typed by the user into the new blood level.
 * <p>
 * This class contains no Minecraft types on purpose, so that it can be unit-tested in plain Java.
 */
public final class BloodCommandUtils {

    /** Maximum of the blood reserve in W:R (see VampireCreatureTrait#fillBloodReserve) **/
    public static final int MAX_RESERVE = 250;

    private static final long LIMIT = 1_000_000_000L;

    private BloodCommandUtils() {}

    /**
     * @param arg what the user typed: an integer, or 'max' / 'empty' (only when setting, not when adding)
     * @param current the current level
     * @param max the maximum level
     * @param relative if true, arg is added to the current level (can be negative), otherwise it replaces it
     * @return the new level, always between 0 and max
     * @throws NumberFormatException if arg is not understood
     */
    public static int resolve(String arg, int current, int max, boolean relative) {
        String text = arg.trim().toLowerCase(Locale.ROOT);
        long value;
        if (!relative && text.equals("max")) value = max;
        else if (!relative && text.equals("empty")) value = 0;
        else value = Long.parseLong(text);

        value = Math.max(-LIMIT, Math.min(LIMIT, value));
        long result = relative ? current + value : value;
        return (int) Math.max(0, Math.min(max, result));
    }

}
