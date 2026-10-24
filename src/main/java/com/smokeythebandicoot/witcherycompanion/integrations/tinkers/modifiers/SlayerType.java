package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.util.CreatureUtil;
import net.msrandom.witchery.util.WitcheryUtils;

/** What the Silvered, Garliced and Demonrend modifiers are effective against. Used by both the tool modifiers (here)
 * and the armor ones (ConArm integration), so that they behave the same. Has no dependencies on other mods **/
public enum SlayerType {

    SILVERED {
        @Override public boolean isTarget(Entity entity) { return CreatureUtil.isWerewolf(entity); }
        @Override public float getDamageBoost() { return ModifiersConfig.silvered_damageBoost; }
        @Override public float getDamageReduction() { return ModifiersConfig.silvered_damageReduction; }
        @Override public int getAttackerFireSeconds() { return ModifiersConfig.silvered_attackerFireSeconds; }
        @Override public int getWearerHarmTickOffset() { return 4; }
        @Override public boolean isWearerHarmed(EntityPlayer player) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            return playerEx.isTransformation(WitcheryCreatureTraits.WEREWOLF);
        }
    },
    GARLICED {
        @Override public boolean isTarget(Entity entity) { return CreatureUtil.isVampire(entity); }
        @Override public float getDamageBoost() { return ModifiersConfig.garliced_damageBoost; }
        @Override public float getDamageReduction() { return ModifiersConfig.garliced_damageReduction; }
        @Override public int getAttackerFireSeconds() { return ModifiersConfig.garliced_attackerFireSeconds; }
        @Override public int getWearerHarmTickOffset() { return 3; }
        @Override public boolean isWearerHarmed(EntityPlayer player) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            return playerEx.isTransformation(WitcheryCreatureTraits.VAMPIRE);
        }
    },
    DEMONREND {
        @Override public boolean isTarget(Entity entity) { return CreatureUtil.isDemonic(entity); }
        @Override public float getDamageBoost() { return ModifiersConfig.demonrend_damageBoost; }
        @Override public float getDamageReduction() { return ModifiersConfig.demonrend_damageReduction; }
    };

    /** True if the entity is the kind of creature that this modifier is effective against **/
    public abstract boolean isTarget(Entity entity);

    /** Multiplier of the damage dealt to targets, when applied to weapons **/
    public abstract float getDamageBoost();

    /** Multiplier of the damage received from targets, when applied to armor **/
    public abstract float getDamageReduction();

    /** Seconds for which the attackers of armor wearers are set on fire. 0 or less disables it **/
    public int getAttackerFireSeconds() {
        return 0;
    }

    /** Some materials burn the creatures they are effective against, when they wear them. Tick (modulo 20) at which it happens **/
    public int getWearerHarmTickOffset() {
        return -1;
    }

    /** True if the player is of a kind that is harmed by wearing this modifier **/
    public boolean isWearerHarmed(EntityPlayer player) {
        return false;
    }
}
