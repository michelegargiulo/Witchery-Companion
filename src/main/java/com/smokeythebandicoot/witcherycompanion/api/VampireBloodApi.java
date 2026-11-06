package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodConfigParser;
import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodProfile;
import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodRuleSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Decides which creatures have blood that a Vampire can drain with the Drink ability, and what happens when it does.
 * <p>
 * Villagers, Village Guards and Players are NOT handled by this API: they keep their own blood logic in W:R.
 * <p>
 * Rules come from three layers. The first layer that has a rule for a creature wins:
 * 1. Custom: rules added at runtime, from Java or from CraftTweaker scripts
 * 2. Config: entries of the 'Vampire - Tweak Blood Sources' config option
 * 3. Default: the W:R behaviour (every EntityAnimal is a blood source). Can be disabled by config or by setAnimalsDrainableByDefault
 * Inside a layer, an exact registry name beats a mod wildcard (modid:*), which beats a class rule.
 * A rule with BloodProfile.BLOCKED (or an amount of zero) makes the creature NOT drainable.
 */
public class VampireBloodApi {

    private static final BloodRuleSet defaultRules = new BloodRuleSet();
    private static final BloodRuleSet configRules = new BloodRuleSet();
    private static final BloodRuleSet customRules = new BloodRuleSet();

    /** If false, the default rule (every EntityAnimal is a blood source) is ignored. Can be changed from scripts **/
    private static volatile boolean animalsDrainableByDefault = true;

    /** Config entries that were last parsed into configRules, used to detect config changes **/
    private static String[] parsedConfig = null;

    static {
        // W:R behaviour: any EntityAnimal can be drunk from, with the default profile
        defaultRules.putClass(EntityAnimal.class, BloodProfile.DEFAULT);
    }


    /** ========== LOOKUP ========== **/

    /** Returns the blood profile of the creature, or null if a Vampire can't drain it with this API **/
    @Nullable
    public static BloodProfile getProfile(@Nullable Entity entity) {
        if (entity == null) return null;
        refreshConfig();

        ResourceLocation key = EntityList.getKey(entity);
        String id = key == null ? null : key.toString();
        Class<?> clazz = entity.getClass();

        BloodProfile profile = customRules.match(id, clazz);
        if (profile == null) profile = configRules.match(id, clazz);
        if (profile == null && areDefaultsEnabled()) profile = defaultRules.match(id, clazz);
        return (profile == null || profile.isBlocked()) ? null : profile;
    }

    public static boolean isDrainable(@Nullable Entity entity) {
        return getProfile(entity) != null;
    }

    /** The default rules (every animal is a blood source) apply only if neither the config nor a script disabled them **/
    public static boolean areDefaultsEnabled() {
        return animalsDrainableByDefault &&
                ModConfig.PatchesConfiguration.TransformationTweaks.vampire_tweakAnimalsAreBloodSources;
    }

    /** Enables or disables the default rule (every animal is a blood source). With the default disabled, only creatures
     * that are explicitly added can be drunk from. NOTE: this is in addition to the config option **/
    public static void setAnimalsDrainableByDefault(boolean enabled) {
        animalsDrainableByDefault = enabled;
    }

    private static synchronized void refreshConfig() {
        String[] current = ModConfig.PatchesConfiguration.TransformationTweaks.vampire_tweakBloodSources;
        if (parsedConfig != null && Arrays.equals(parsedConfig, current)) return;

        configRules.clear();
        List<String> warnings = BloodConfigParser.parse(current, configRules);
        for (String warning : warnings) {
            WitcheryCompanion.logger.warn("[Vampire Blood Sources] " + warning);
        }
        parsedConfig = current == null ? new String[0] : current.clone();
    }


    /** ========== CUSTOM RULES ========== **/

    private static BloodProfile profileOf(int amount, int maxFillPercent, float damage) {
        return amount <= 0 ? BloodProfile.BLOCKED : new BloodProfile(amount, Math.max(1, maxFillPercent), damage);
    }

    private static String normalizeId(String id) {
        String lower = id.trim().toLowerCase(Locale.ROOT);
        return lower.indexOf(':') < 0 ? "minecraft:" + lower : lower;
    }

    /**
     * Makes a creature a blood source, replacing any previous custom rule for the same registry name
     * @param entityId registry name of the creature (for example minecraft:cow)
     * @param amount blood power restored by each drink. Zero or less means the creature can NOT be drained
     * @param maxFillPercent the vampire can't be brought above this percentage (1-100) of its max blood power
     * @param damage magic damage dealt to the creature by each drink
     */
    public static void addEntity(String entityId, int amount, int maxFillPercent, float damage) {
        customRules.putId(normalizeId(entityId), profileOf(amount, maxFillPercent, damage));
    }

    public static void addEntity(String entityId, int amount, int maxFillPercent) {
        addEntity(entityId, amount, maxFillPercent, BloodProfile.DEFAULT_DAMAGE);
    }

    public static void addEntity(String entityId, int amount) {
        addEntity(entityId, amount, BloodProfile.DEFAULT_MAX_FILL_PERCENT, BloodProfile.DEFAULT_DAMAGE);
    }

    /** Makes a creature a blood source with the default profile (same as W:R animals) **/
    public static void addEntity(String entityId) {
        customRules.putId(normalizeId(entityId), BloodProfile.DEFAULT);
    }

    /** Makes a creature NOT a blood source, even if it would be by default (like any EntityAnimal) **/
    public static void removeEntity(String entityId) {
        customRules.putId(normalizeId(entityId), BloodProfile.BLOCKED);
    }

    /** Makes every creature of a mod (that has no more specific rule) a blood source **/
    public static void addMod(String modId, int amount, int maxFillPercent, float damage) {
        customRules.putNamespace(modId.trim().toLowerCase(Locale.ROOT), profileOf(amount, maxFillPercent, damage));
    }

    /** Makes every creature of a mod (that has no more specific rule) NOT a blood source **/
    public static void removeMod(String modId) {
        customRules.putNamespace(modId.trim().toLowerCase(Locale.ROOT), BloodProfile.BLOCKED);
    }

    /** Makes every creature that is an instance of the class (and has no more specific rule) a blood source **/
    public static void addEntityClass(Class<? extends Entity> entityClass, int amount, int maxFillPercent, float damage) {
        customRules.putClass(entityClass, profileOf(amount, maxFillPercent, damage));
    }

    /** Makes every creature that is an instance of the class (and has no more specific rule) NOT a blood source **/
    public static void removeEntityClass(Class<? extends Entity> entityClass) {
        customRules.putClass(entityClass, BloodProfile.BLOCKED);
    }

}
