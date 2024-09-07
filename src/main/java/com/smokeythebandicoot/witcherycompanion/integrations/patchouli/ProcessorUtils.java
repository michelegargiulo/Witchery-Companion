package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.google.gson.*;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessorUtils {

    private static final Map<String, KeyInfo> keyCache = new HashMap<>();

    /** The main JSON serializer.
     * Implements custom methods to serialize and deserialize common fields used in DTOs that
     * are passed from each TemplateProcessor to the corresponding ICustomComponent */
    private static final Gson gson = new GsonBuilder()

            // Ingredient serializer
            .registerTypeAdapter(Ingredient.class, (JsonSerializer<Ingredient>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(ItemStackUtil.serializeIngredient(src == null ? Ingredient.EMPTY : src)))

            // Ingredient deserializer
            .registerTypeAdapter(Ingredient.class, (JsonDeserializer<Ingredient>) (src, typeOfSrc, context) -> {
                if (src == null) return Ingredient.EMPTY;
                return ItemStackUtil.loadIngredientFromString(src.getAsString());
            })

            // ItemStack serializer
            .registerTypeAdapter(ItemStack.class, (JsonSerializer<ItemStack>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(ItemStackUtil.serializeStack(src == null ? ItemStack.EMPTY : src)))

            // ItemStack deserializer
            .registerTypeAdapter(ItemStack.class, (JsonDeserializer<ItemStack>) (src, typeOfSrc, context) -> {
                if (src == null) return ItemStack.EMPTY;
                return ItemStackUtil.loadStackFromString(src.getAsString());
            })

            // Serialize only minimal fields (annotated with @Expose)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    /** Takes a key and a prefix and extracts the index. Useful for when a component has key1, key2,
     * key3, ... , keyN and you want to extract the index (1, 2, 3, ... , N) from the key **/
    public static KeyInfo splitKeyIndex(String key) {
        if (keyCache.containsKey(key))
            return keyCache.get(key);
        KeyInfo info = new KeyInfo(key);
        keyCache.put(key, info);
        return info;
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
        if (stripFormatting) {
            formattedString = formattedString
                .replaceAll("§[0-9a-fklmnor]", "")
                .replaceAll("\\$\\([0-9a-fklmnor]\\)", "");
        }
        return formattedString
                .trim()
                .replace("\n", "$(br)");
    }

    /** Reads a variable from a provider */
    public static String readVariable(IVariableProvider<String> provider, String key) {
        return provider.has(key) ? provider.get(key) : null;
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
            deserializeIngredientList(elem, ingredients);
        }
    }

    public static void deserializeIngredientList(String serialized, @Nonnull Collection<Ingredient> ingredients) {
        for (String serializedIngredient : serialized.replace(" ", "").split(",")) {
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



    public static String serializeDto(AbstractDTO dto) {
        return gson.toJson(dto);
    }

    public static <T extends AbstractDTO> T deserializeDto(String str, Type t) {
        try {
            return (T) gson.<T>fromJson(str, t);
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Failed deserialization of DTO {}. Json: {}. Reason: {}",
                    t.getTypeName(), str, ex);
        }
        return null;
    }

    public static <T> T deserializeDto(String str, Class<? extends AbstractDTO> clazz) {
        try {
            return (T) gson.<T>fromJson(str, clazz);
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Failed deserialization of DTO {}. Json: {}. Reason: {}",
                    clazz.getSimpleName(), str, ex);
        }
        return null;
    }


    public static class KeyInfo {

        // Regex pattern to recognize trailing numbers at the end of an alpha-numerical string. The goal is to split
        // the string in two groups, the first being an alpha-numerical string, the second being a string convertible to an int
        // Matches:
        // - The last number in the string, removing any leading zeroes (causes exception with Integer.parseInt)
        // - The last digit 0 at the end of the string
        // Negative numbers not supported
        private static final Pattern keySplitterPattern = Pattern.compile("(^.*?)([0-9]|[1-9]\\d*)$");

        public final String key;
        public final int index;

        public KeyInfo(String key) {
            Matcher m = keySplitterPattern.matcher(key);
            if (m.find()) {
                this.key = m.group(1);
                this.index = Integer.parseInt(m.group(2));
            } else {
                this.key = key;
                this.index = -1;
            }
        }
    }

}
