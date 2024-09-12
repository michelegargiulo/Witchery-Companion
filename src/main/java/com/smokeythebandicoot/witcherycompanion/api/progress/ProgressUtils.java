package com.smokeythebandicoot.witcherycompanion.api.progress;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ProgressUtils {

    // Defined format for Capacity Brew secret items: MODID:brewing/capacity/<namespace>:<path>:<meta>
    public static String getBrewActionSecret(@Nonnull ItemStack stack) {
        return WitcheryCompanion.prefix("brewing/items/" + stack.getItem().getRegistryName() + ":" + stack.getMetadata());
    }

    // Defined format for Secret Brazier Recipes: MODID:brazier/recipe/<recipeID>
    public static String getBrazierRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("brazier/recipe/" + recipeId);
    }

    // Defined format for Secret Infused Spirit Effects: MODID:brazier/effect/<recipeID>
    public static String getSpiritEffectRecipeSecret(@Nonnull String effectId) {
        return WitcheryCompanion.prefix("conjuring/effect/" + effectId);
    }

    // Defined format for Symbol Effects: MODID:symbology/symbol/<recipeID>
    public static String getSymbolEffectSecret(@Nonnull String symbolId) {
        return WitcheryCompanion.prefix("symbology/symbol/" + symbolId);
    }

    // Defined format for Secret Rite Effects: MODID:circles/rite_effect/<riteID>
    public static String getRiteEffectSecret(@Nonnull String riteId) {
        return WitcheryCompanion.prefix("circles/rite_effect/" + riteId);
    }

    // Defined format for Distillery Recipe: MODID:distillery/recipe/<recipeId>
    public static String getDistilleryRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("distillery/recipe/" + recipeId);
    }

    // Defined format for Distillery Recipe: MODID:distillery/recipe/<recipeId>
    public static String getCauldronRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("cauldron/recipe/" + recipeId);
    }

}
