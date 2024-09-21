package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessorUtils {

    private static final Pattern keySplitterPattern = Pattern.compile("(^.*?)([0-9]|[1-9]\\d*)$");
    private static final Map<String, Integer> keyCache = new HashMap<>();

    /** Takes a key and extracts the index. Useful for when a component has key1, key2,
     * key3, ... , keyN and you want to extract the index (1, 2, 3, ... , N) from the key **/
    public static int splitKeyIndex(String key) {
        if (keyCache.containsKey(key))
            return keyCache.get(key);

        int index = -1;
        Matcher m = keySplitterPattern.matcher(key);
        if (m.find()) {
            try {
                index = Integer.parseInt(m.group(2));
            } catch (Exception ignored) { }
        }

        keyCache.put(key, index);
        return index;
    }

    /** Makes so that a serialized stack always contains meta information and, if != 1, also amount.
     * For example "minecraft:nether_wart" will became "minecraft:nether_wart:0", following Patchouli conventions **/
    public static String getCanonic(String stack) {
        if (stack == null)
            return null;
        return ItemStackUtil.serializeIngredient(ItemStackUtil.loadIngredientFromString(stack));
    }

    /** Strips a string of any leading or trailing whitespaces, removes Minecraft formatting
     * and converts line feeds to Patchouli line breaks **/
    public static String reformatPatchouli(String formattedString, boolean stripFormatting) {
        if (formattedString == null) return null;
        if (stripFormatting) {
            formattedString = formattedString
                .replaceAll("§[0-9a-fklmnor]", "")
                .replaceAll("\\$\\([0-9a-fklmnor]?\\)", "")
                .replaceAll("\\$\\((item|thing|nocolor|obf|strike|italic|italics|bold|#[a-z0-9]*)\\)", "");
        }
        return formattedString
                .trim()
                .replace("\n", "$(br)");
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
                sb.append(";");
            }
        }
        return sb.toString();
    }

    public static void deserializeIngredientList(String[] serialized, @Nonnull Collection<Ingredient> ingredients) {
        for (String elem : serialized) {
            deserializeIngredientList(elem, ingredients);
        }
    }

    public static void deserializeIngredientList(String serialized, @Nonnull Collection<Ingredient> ingredients) {
        for (String serializedIngredient : serialized.replace(" ", "").split(";")) {
            ingredients.add(ItemStackUtil.loadIngredientFromString(serializedIngredient));
        }
    }



    /** Takes a collection of ingredients and serializes them in a string. Each element will be separated
     * by a comma (,) and serialized using Patchouli conventions **/
    public static String serializeItemStackList(List<ItemStack> stacks) {
        if (stacks == null)
            return "";
        StringBuilder sb = new StringBuilder();
        Iterator<ItemStack> it = stacks.iterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            sb.append(ItemStackUtil.serializeStack(stack));
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static List<ItemStack> deserializeItemStackList(String[] serialized) {
        if (serialized == null) return null;
        List<ItemStack> stacks = new ArrayList<>();
        for (String elem : serialized) {
            for (String str : elem.replace(" ", "").split(",")) {
                stacks.add(ItemStackUtil.loadStackFromString(str));
            }
        }
        return stacks;
    }

    public static List<ItemStack> deserializeItemStackList(String serialized) {
        if (serialized == null) return null;
        List<ItemStack> stacks = new ArrayList<>();
        for (String serializedStack : serialized.replace(" ", "").split(",")) {
            stacks.add(ItemStackUtil.loadStackFromString(serializedStack));
        }
        return stacks;
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
