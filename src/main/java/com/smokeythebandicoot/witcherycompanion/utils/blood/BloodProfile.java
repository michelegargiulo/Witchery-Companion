package com.smokeythebandicoot.witcherycompanion.utils.blood;

/**
 * Describes what happens when a Vampire drinks from a creature that is not human (Villagers, Village Guards and
 * Players have their own, stateful, blood logic in W:R and are not handled here).
 * <p>
 * Defaults replicate the hardcoded W:R behaviour for animals: each drink deals 1 magic damage, restores 2 blood
 * points and cannot raise the Vampire's blood above 25% of the maximum blood power.
 */
public final class BloodProfile {

    public static final int DEFAULT_AMOUNT = 2;
    public static final int DEFAULT_MAX_FILL_PERCENT = 25;
    public static final float DEFAULT_DAMAGE = 1.0f;

    /** Profile that replicates the W:R behaviour for animals **/
    public static final BloodProfile DEFAULT = new BloodProfile(DEFAULT_AMOUNT, DEFAULT_MAX_FILL_PERCENT, DEFAULT_DAMAGE);

    /** Sentinel: a creature with this profile is explicitly NOT a blood source, and overrides any lower-priority rule **/
    public static final BloodProfile BLOCKED = new BloodProfile(0, 0, 0.0f);

    /** Blood power restored by each drink **/
    public final int amount;
    /** The Vampire can't be raised above this percentage (1-100) of its max blood power by drinking this creature **/
    public final int maxFillPercent;
    /** Magic damage dealt to the creature by each drink. Zero means the creature is not hurt **/
    public final float damage;


    public BloodProfile(int amount, int maxFillPercent, float damage) {
        this.amount = Math.max(0, amount);
        this.maxFillPercent = Math.min(100, Math.max(0, maxFillPercent));
        this.damage = (Float.isNaN(damage) || damage < 0.0f) ? 0.0f : damage;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }

    /** Returns the maximum blood power that drinking this creature can bring a vampire to **/
    public int capFor(int maxBloodPower) {
        return (int) Math.ceil(maxBloodPower * (this.maxFillPercent / 100.0));
    }

    /**
     * Returns the new blood power of a Vampire after drinking once from this creature. Same algorithm as
     * W:R VampireCreatureTrait#drinkAnimalBlood, with configurable amount and cap.
     **/
    public int drink(int currentBloodPower, int maxBloodPower) {
        int cap = capFor(maxBloodPower);
        if (currentBloodPower < maxBloodPower && currentBloodPower < cap) {
            return Math.min(currentBloodPower + this.amount, cap);
        }
        return currentBloodPower;
    }

    @Override
    public String toString() {
        return this == BLOCKED ? "BloodProfile[blocked]" :
                "BloodProfile[amount=" + amount + ", maxFillPercent=" + maxFillPercent + ", damage=" + damage + "]";
    }

}
