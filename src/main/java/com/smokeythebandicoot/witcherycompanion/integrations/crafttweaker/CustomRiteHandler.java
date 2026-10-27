package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Entry point of the Custom Rites in ZenScript: CustomRite.create("id") starts the definition of a rite */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.CustomRite")
@ZenRegister
public class CustomRiteHandler {

    @ZenMethod
    @ZenDoc(value = "Starts the definition of a custom rite. The id is 'namespace:name' (the namespace defaults to " +
            "witcherycompanion; only a-z 0-9 _ . - are allowed). Nothing is added until register() is called on the " +
            "builder. Defining a rite with an existing custom id replaces it")
    public static CustomRiteBuilder create(String id) {
        if (!ModConfig.IntegrationConfigurations.CraftTweakerIntegration.enableCustomRites) {
            CraftTweakerAPI.logWarning("Custom Rites are disabled in the Witchery: Companion config. Rite '" + id + "' will be ignored");
            return new CustomRiteBuilder(null);
        }
        try {
            return new CustomRiteBuilder(id);
        } catch (IllegalArgumentException e) {
            CraftTweakerAPI.logError("Cannot create custom rite: " + e.getMessage());
            return new CustomRiteBuilder(null);
        }
    }

}
