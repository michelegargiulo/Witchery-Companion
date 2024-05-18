package com.smokeythebandicoot.witcherypatcher.integrations.crafttweaker.bridge;


import net.minecraft.village.MerchantRecipeList;

/**
 * This class is responsible to Bridge between Witchery and Integrations (Crafttweaker, Groovy (eventually))
 * It keeps track of the Goblin trades and provides them to Witchery. Integrations can modify this class
 * Mixins will inject the trades contained here in the EntityGoblin class
 */
public class GoblinTradeBridge {

    public MerchantRecipeList getTrades(int profession) {

    }



}
