package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.VampireBloodApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodProfile;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;


@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.VampireBlood")
@ZenRegister
public class VampireBloodHandler {

    private static boolean isEnabled() {
        if (!ModConfig.PatchesConfiguration.TransformationTweaks.vampire_tweakCustomBloodSources) {
            CraftTweakerAPI.logWarning("VampireBlood is disabled in the Witchery: Companion config " +
                    "('Vampire - Tweak Custom Blood Sources'). The call will be ignored");
            return false;
        }
        return true;
    }


    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source: a Vampire can drink from it. 'amount' is the blood restored by each " +
            "drink (default 2), 'maxFillPercent' the percentage (1-100) of max blood that this creature can fill up to " +
            "(default 25), 'damage' the magic damage dealt to the creature by each drink (default 1). An amount of zero " +
            "or less makes the creature NOT a blood source. Overrides the config. Villagers, Village Guards and Players " +
            "are not affected")
    public static void addEntity(IEntityDefinition entity, int amount, int maxFillPercent, double damage) {
        if (isEnabled()) VampireBloodApi.addEntity(entity.getId(), amount, maxFillPercent, (float) damage);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, with the given amount of blood for each drink")
    public static void addEntity(IEntityDefinition entity, int amount, int maxFillPercent) {
        if (isEnabled()) VampireBloodApi.addEntity(entity.getId(), amount, maxFillPercent);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, with the given amount of blood for each drink")
    public static void addEntity(IEntityDefinition entity, int amount) {
        if (isEnabled()) VampireBloodApi.addEntity(entity.getId(), amount);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, with the same blood settings of W:R animals")
    public static void addEntity(IEntityDefinition entity) {
        if (isEnabled()) VampireBloodApi.addEntity(entity.getId());
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, using its registry name (for example 'minecraft:zombie'). " +
            "See the other addEntity for the meaning of the parameters")
    public static void addEntity(String entityId, int amount, int maxFillPercent, double damage) {
        if (isEnabled()) VampireBloodApi.addEntity(entityId, amount, maxFillPercent, (float) damage);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, using its registry name")
    public static void addEntity(String entityId, int amount, int maxFillPercent) {
        if (isEnabled()) VampireBloodApi.addEntity(entityId, amount, maxFillPercent);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, using its registry name")
    public static void addEntity(String entityId, int amount) {
        if (isEnabled()) VampireBloodApi.addEntity(entityId, amount);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature a blood source, using its registry name")
    public static void addEntity(String entityId) {
        if (isEnabled()) VampireBloodApi.addEntity(entityId);
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature NOT a blood source, even if W:R would normally allow it (for example any animal)")
    public static void removeEntity(IEntityDefinition entity) {
        if (isEnabled()) VampireBloodApi.removeEntity(entity.getId());
    }

    @ZenMethod
    @ZenDoc(value = "Makes a creature NOT a blood source, using its registry name")
    public static void removeEntity(String entityId) {
        if (isEnabled()) VampireBloodApi.removeEntity(entityId);
    }

    @ZenMethod
    @ZenDoc(value = "Makes every creature added by a mod (for example 'twilightforest') a blood source, unless it " +
            "has a more specific entry. See addEntity for the meaning of the parameters")
    public static void addMod(String modId, int amount, int maxFillPercent, double damage) {
        if (isEnabled()) VampireBloodApi.addMod(modId, amount, maxFillPercent, (float) damage);
    }

    @ZenMethod
    @ZenDoc(value = "Makes every creature added by a mod a blood source, unless it has a more specific entry")
    public static void addMod(String modId, int amount) {
        if (isEnabled()) VampireBloodApi.addMod(modId, amount, BloodProfile.DEFAULT_MAX_FILL_PERCENT, BloodProfile.DEFAULT_DAMAGE);
    }

    @ZenMethod
    @ZenDoc(value = "If false, animals are no longer blood sources by default (W:R lets a Vampire drink from any animal): only the creatures that are " +
            "explicitly added can be drunk from. Animals added with addEntity or addMod still work. Default is true. " +
            "The config option 'Vampire - Tweak All Animals Are Blood Sources' also has to be true")
    public static void setAnimalsDrainable(boolean enabled) {
        if (isEnabled()) VampireBloodApi.setAnimalsDrainableByDefault(enabled);
    }

    @ZenMethod
    @ZenDoc(value = "Makes every creature added by a mod NOT a blood source, unless it has a more specific entry")
    public static void removeMod(String modId) {
        if (isEnabled()) VampireBloodApi.removeMod(modId);
    }

}
