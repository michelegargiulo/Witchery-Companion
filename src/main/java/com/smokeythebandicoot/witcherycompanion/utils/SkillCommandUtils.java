package com.smokeythebandicoot.witcherycompanion.utils;

import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodCommandUtils;

/**
 * Pure logic of the skill command: turns the value typed by the user into the new skill level. No Minecraft types, so that it
 * can be unit-tested in plain Java.
 */
public final class SkillCommandUtils {

    /** Maximum of the bottling skill (see PlayerExtendedData#increaseBottlingSkill), and of the throwing skill */
    public static final int MAX_SKILL = 100;

    private SkillCommandUtils() {}

    /**
     * @param arg what the user typed: an integer, or 'max' / 'empty' (only when setting, not when adding)
     * @param current the current skill
     * @param relative if true, arg is added to the current skill (can be negative), otherwise it replaces it
     * @return the new skill, always between 0 and {@link #MAX_SKILL}
     * @throws NumberFormatException if arg is not understood
     */
    public static int resolve(String arg, int current, boolean relative) {
        return BloodCommandUtils.resolve(arg, current, MAX_SKILL, relative);
    }

}
