package com.smokeythebandicoot.witcherycompanion.api.progress;

import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;

import java.util.HashSet;
import java.util.Set;

public class ProgressManager {


    private static Set<String> secrets;


    private ProgressManager() { }


    public static void discoverSecrets() {
        secrets = new HashSet<>();

        // Discover secret BrewAction
        discoverBrewActions();

        // Discover secret SymbolEffect

        // Discover secret BrazierRecipe

        // Discover secret InfusedSpiritEffect

        // Discover secret RiteEffect

        // Discover hard-coded secrets
        // Summon Death
        // Player summon as Death

    }

    public static void listSecrets() {

    }

    public static void explainSecret() {

    }



    private static void discoverBrewActions() {
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action.getHidden()) {
                if (action instanceof CapacityBrewAction) {
                    secrets.add(ProgressUtils.getCapacityBrewAction(action.getKey().toStack()));
                } else {
                    secrets.add(ProgressUtils.getGenericBrewActionSecret(action.getKey().toStack()));
                }
            }
        }
    }
}
