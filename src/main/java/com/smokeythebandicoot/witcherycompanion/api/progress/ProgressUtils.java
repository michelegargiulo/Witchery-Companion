package com.smokeythebandicoot.witcherycompanion.api.progress;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;

public class ProgressUtils {

    // Defined format for Capacity Brew secret items: MODID:brewing/capacity/<namespace>:<path>:<meta>
    public static String getCapacityBrewAction(ItemStack stack) {
        if (stack == null)
            return null;
        return WitcheryCompanion.prefix("brewing/capacity/" + stack.getItem().getRegistryName() + ":" + stack.getMetadata());
    }

    // Defined format for Capacity Brew secret items: MODID:brewing/capacity/<namespace>:<path>:<meta>
    public static String getGenericBrewActionSecret(ItemStack stack) {
        if (stack == null)
            return null;
        return WitcheryCompanion.prefix("brewing/generic/" + stack.getItem().getRegistryName() + ":" + stack.getMetadata());
    }

    // Defined format for Secret Brazier Recipes: MODID:brazier/recipe/<recipeID>
    public static String getBrazierRecipeSecret(String recipeId) {
        if (recipeId == null)
            return null;
        return WitcheryCompanion.prefix("brazier/recipe/" + recipeId);
    }

    // Defined format for Secret Infused Spirit Effects: MODID:brazier/effect/<recipeID>
    public static String getSpiritEffectRecipeSecret(String effectId) {
        if (effectId == null)
            return null;
        return WitcheryCompanion.prefix("conjuring/effect/" + effectId);
    }

    // Defined format for Symbol Effects: MODID:symbology/symbol/<recipeID>
    public static String getSymbolEffectSecret(String symbolId) {
        if (symbolId == null)
            return null;
        return WitcheryCompanion.prefix("symbology/symbol/" + symbolId);
    }

    // Defined format for Secret Rite Effects: MODID:circles/rite_effect/<riteID>
    public static String getRiteEffectSecret(String riteId) {
        if (riteId == null)
            return null;
        return WitcheryCompanion.prefix("circles/rite_effect/" + riteId);
    }

}
