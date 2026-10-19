package com.smokeythebandicoot.witcherycompanion.utils;

/** Pure logic (no Minecraft types) for cooldowns that are expressed as "elapsed ticks must be greater than the cooldown" */
public class CooldownUtils {

    /**
     * Ticks left before an action becomes available again, for a check done like W:R does:
     * available when (now - lastUsed) > cooldown. So it is 0 or less when it is available
     */
    public static long remainingTicks(long lastUsedTime, long now, long cooldown) {
        return lastUsedTime + cooldown + 1L - now;
    }

    /** Converts ticks to whole seconds, rounding up, so that the countdown never shows 0s while still on cooldown */
    public static long ticksToSecondsCeil(long ticks) {
        return ticks <= 0 ? 0 : (ticks + 19L) / 20L;
    }

}
