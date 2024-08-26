package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ProcessorUtils {

    /** Takes a key and a prefix and extracts the index. Useful for when a component has key1, key2,
     * key3, ... , keyN and you want to extract the index (1, 2, 3, ... , N) from the key **/
    public static int getIndexFromKey(String key, String prefix) {
        try {
            return Integer.parseInt(key.substring(prefix.length(), prefix.length() + 1));
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Could not parse the index for key: {}", key);
        }
        return -1;
    }

    /** Makes so that a serialized stack always contains meta information and, if != 1, also amount.
     * For example "minecraft:nether_wart" will became "minecraft:nether_wart:0", following Patchouli conventions **/
    public static String getCanonic(String stack) {
        if (stack == null)
            return null;
        return ItemStackUtil.serializeIngredient(ItemStackUtil.loadIngredientFromString(stack));
    }

    /** Takes a collection of ingredients and serializes them in a string. Each element will be separated
     * by a comma (,) and serialized using Patchouli conventions **/
    public static String serializeIngredientList(Collection<Ingredient> ingredients) {
        if (ingredients == null)
            return "";
        StringBuilder sb = new StringBuilder();
        Iterator<Ingredient> it = ingredients.iterator();
        while (it.hasNext()) {
            Ingredient ing = it.next();
            sb.append(ItemStackUtil.serializeIngredient(ing));
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void deserializeIngredientList(String[] serialized, @Nonnull Collection<Ingredient> ingredients) {
        for (String elem : serialized) {
            for (String serializedIngredient : elem.replace(" ", "").split(",")) {
                ingredients.add(ItemStackUtil.loadIngredientFromString(serializedIngredient));
            }
        }
    }

    /** Takes a collection of StrokeInfo and serializes them in a string. Format is:
     * stroke1, stroke2, ... , strokeN etc. Whitespaces are ignored **/
    public static String serializeStrokeArray(StrokeArray strokes) {
        StringBuilder sb = new StringBuilder();
        Iterator<BranchStroke> it = strokes.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext())
                sb.append(",");
        }
        return sb.toString();
    }

    public static void deserializeStrokeArray(String[] serialized, @Nonnull Collection<BranchStroke> strokes) {
        for (String elem : serialized) {
            for (String s : elem.replace(" ", "").split(",")) {
                try {
                    strokes.add(BranchStroke.valueOf(s.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    WitcheryCompanion.logger.warn("Could not deserialize symbol branch stroke: {}", s);
                }
            }
        }
    }
}
