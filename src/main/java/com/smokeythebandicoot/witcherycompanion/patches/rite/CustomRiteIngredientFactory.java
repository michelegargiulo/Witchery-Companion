package com.smokeythebandicoot.witcherycompanion.patches.rite;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.smokeythebandicoot.witcherycompanion.api.CustomRiteApi;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

/**
 * Forge ingredient factory for the items of custom rites. W:R reads the ingredient of a sacrifice with Forge's
 * CraftingHelper, so a JSON {"type": "witcherycompanion:custom_rite_ingredient", "key": "..."} resolves to the Ingredient
 * that the script defined, with its own matching logic, instead of one rebuilt from data.
 */
public class CustomRiteIngredientFactory implements IIngredientFactory {

    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json) {
        if (json.get("key") == null) throw new JsonSyntaxException("Custom rite ingredient without a key");
        String key = json.get("key").getAsString();
        Ingredient ingredient = CustomRiteApi.getIngredient(key);
        if (ingredient == null) throw new JsonSyntaxException("Unknown custom rite ingredient " + key);
        return ingredient;
    }

}
