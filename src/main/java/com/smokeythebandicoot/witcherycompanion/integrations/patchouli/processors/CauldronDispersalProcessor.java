package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import java.util.Map;

public class CauldronDispersalProcessor {

    private static Map<String, Ingredient> dispersalMap;

    //TODO: make this dynamic
    public static Ingredient getDispersal(String dispersalId) {
        if (dispersalId.equals("instant")) {
            return Ingredient.fromItems(WitcheryIngredientItems.ARTICHOKE, Items.GUNPOWDER);
        }
        return Ingredient.fromItems();
    }
}
