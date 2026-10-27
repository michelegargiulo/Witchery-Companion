package com.smokeythebandicoot.witcherycompanion.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteDefinition;
import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteEffectHandler;
import com.smokeythebandicoot.witcherycompanion.patches.rite.CustomRiteIngredientFactory;
import com.smokeythebandicoot.witcherycompanion.patches.rite.RiteEffectCustomRite;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.msrandom.witchery.rite.Rite;
import net.msrandom.witchery.rite.effect.RiteEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry of the custom Rites. Definitions are registered (by CraftTweaker, or by any other mod) before the world
 * loads. When W:R's RiteManager reloads its data, the definitions are appended to the JSON that W:R is about to read
 * (see RiteManagerMixin), so that the rites go through W:R's own parsing and are synced to the clients like any other
 * rite, which also makes them appear in the W:R Rites book.
 */
public class CustomRiteApi {

    /** Max number of custom rites that have a page in the Witches' Companion book (see the book entry) */
    public static final int BOOK_SLOTS = 30;

    private static final class Registered {
        final CustomRiteDefinition definition;
        final CustomRiteEffectHandler handler;
        final Map<String, Ingredient> ingredients;

        Registered(CustomRiteDefinition definition, CustomRiteEffectHandler handler, Map<String, Ingredient> ingredients) {
            this.definition = definition;
            this.handler = handler;
            this.ingredients = ingredients;
        }
    }

    private static final Map<String, Registered> RITES = new LinkedHashMap<>();
    /** The custom rites as parsed by RiteManager.apply(). See restoreParsedRites */
    private static final Map<ResourceLocation, Rite> PARSED = new LinkedHashMap<>();
    private static boolean gluesRegistered = false;

    private CustomRiteApi() { }

    /**
     * Registers a custom rite. An existing rite with the same id is replaced.
     * @param ingredients ingredients that the items of the definition refer to with CustomRiteDefinition.ingredientRef,
     *                    by key. They decide which items match (so they can match metadata, NBT, anything)
     * @return the list of problems found. If not empty, the rite has NOT been registered
     */
    public static List<String> register(CustomRiteDefinition definition, CustomRiteEffectHandler handler, Map<String, Ingredient> ingredients) {
        List<String> problems = definition.validate();
        if (handler == null) problems.add("the rite has no effect (use setEffect)");
        if (!problems.isEmpty()) return problems;

        Registered previous = RITES.put(definition.getId(), new Registered(definition, handler, new LinkedHashMap<>(ingredients)));
        if (previous != null) {
            WitcheryCompanion.logger.warn("Custom Rite {} was registered twice: the previous definition is replaced", definition.getId());
        }
        return Collections.emptyList();
    }

    public static boolean unregister(String id) {
        return RITES.remove(CustomRiteDefinition.normalizeId(id)) != null;
    }

    @Nullable
    public static CustomRiteDefinition getDefinition(String id) {
        Registered registered = RITES.get(id);
        return registered == null ? null : registered.definition;
    }

    @Nullable
    public static CustomRiteEffectHandler getHandler(String id) {
        Registered registered = RITES.get(id);
        return registered == null ? null : registered.handler;
    }

    /** Ingredient held for an item of a rite, by key (see CustomRiteDefinition.ingredientKey). Null if unknown */
    @Nullable
    public static Ingredient getIngredient(String key) {
        int separator = key.lastIndexOf('#');
        if (separator < 0) return null;
        Registered registered = RITES.get(key.substring(0, separator));
        return registered == null ? null : registered.ingredients.get(key);
    }

    public static Collection<CustomRiteDefinition> getDefinitions() {
        List<CustomRiteDefinition> definitions = new ArrayList<>();
        for (Registered registered : RITES.values()) definitions.add(registered.definition);
        return definitions;
    }

    public static boolean isEmpty() {
        return RITES.isEmpty();
    }

    // ------------------------------------------------------------------------------------------ W:R glue

    /**
     * Returns a copy of the rite data that W:R is about to load, with the custom rites added at the end.
     * The effect type used by custom rites is registered in W:R's registry the first time this is called.
     */
    public static Map<ResourceLocation, JsonElement> appendCustomRites(Map<ResourceLocation, ? extends JsonElement> original) {
        Map<ResourceLocation, JsonElement> result = new LinkedHashMap<>(original);
        if (RITES.isEmpty()) return result;

        ensureGluesRegistered();
        for (Registered registered : RITES.values()) {
            CustomRiteDefinition definition = registered.definition;
            ResourceLocation key = new ResourceLocation(definition.getDataKey());
            if (result.containsKey(key)) {
                WitcheryCompanion.logger.warn("Custom Rite {} overrides the existing data entry {}", definition.getId(), key);
            }
            result.put(key, toGson(definition.toRiteJson()));
        }
        return result;
    }

    /** Registers, once, the effect type and the ingredient type that the JSON of custom rites refers to */
    /**
     * Remembers the Rite objects that W:R built from the custom rites. Called when RiteManager.apply() has finished.
     * @param riteMap the rites of the RiteManager (rite id -> rite)
     */
    public static void rememberParsedRites(Map<ResourceLocation, Rite> riteMap) {
        PARSED.clear();
        for (Registered registered : RITES.values()) {
            ResourceLocation id = new ResourceLocation(registered.definition.getId());
            Rite rite = riteMap.get(id);
            if (rite != null) PARSED.put(id, rite);
        }
        if (isDebugEnabled()) {
            WitcheryCompanion.logger.info("[Custom Rites debug] {} custom rite(s) parsed by W:R: {}", PARSED.size(), PARSED.keySet());
        }
    }

    /**
     * W:R sends the rites to the clients, which replace their rites with the ones read from the packet. In the
     * singleplayer (and when hosting to LAN) client and server share the RiteManager, so the packet would replace the
     * rites parsed by the server with copies in which the ingredients are plain item lists: the NBT, metadata and script
     * conditions of the ingredients of custom rites would be lost. When a server runs in this JVM, the custom rites in
     * the packet are replaced with the ones that W:R parsed from the data, so the server keeps matching with the
     * ingredients defined by the scripts. A client of a remote server has no such rites and gets them from the packet.
     */
    public static Map<ResourceLocation, List<Rite>> restoreParsedRites(Map<ResourceLocation, List<Rite>> incoming) {
        if (PARSED.isEmpty() || FMLCommonHandler.instance().getMinecraftServerInstance() == null) return incoming;

        Map<ResourceLocation, List<Rite>> result = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, List<Rite>> group : incoming.entrySet()) {
            List<Rite> rites = new ArrayList<>();
            for (Rite rite : group.getValue()) {
                Rite parsed = PARSED.get(rite.getId());
                rites.add(parsed != null ? parsed : rite);
            }
            result.put(group.getKey(), rites);
        }
        return result;
    }

    /** True if the diagnostic logging of custom rites is enabled in the config */
    public static boolean isDebugEnabled() {
        return com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.CraftTweakerIntegration.debugCustomRites;
    }

    private static void ensureGluesRegistered() {
        if (gluesRegistered) return;
        ResourceLocation effectType = new ResourceLocation(CustomRiteDefinition.EFFECT_TYPE);
        if (!RiteEffect.DESERIALIZER_REGISTRY.contains(effectType)) {
            RiteEffect.DESERIALIZER_REGISTRY.set(effectType, RiteEffectCustomRite.DESERIALIZER);
        }
        try {
            CraftingHelper.register(new ResourceLocation(CustomRiteDefinition.INGREDIENT_TYPE), new CustomRiteIngredientFactory());
        } catch (IllegalStateException e) {
            // Already registered (Forge does not allow to query its table): nothing to do
        }
        gluesRegistered = true;
    }

    /** Converts the Map/List/String/Number/Boolean tree built by CustomRiteDefinition into Gson */
    @SuppressWarnings("unchecked")
    static JsonElement toGson(Object node) {
        if (node == null) return JsonNull.INSTANCE;
        if (node instanceof Map) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) node).entrySet()) {
                object.add(entry.getKey(), toGson(entry.getValue()));
            }
            return object;
        }
        if (node instanceof Iterable) {
            JsonArray array = new JsonArray();
            for (Object element : (Iterable<Object>) node) array.add(toGson(element));
            return array;
        }
        if (node instanceof Boolean) return new JsonPrimitive((Boolean) node);
        if (node instanceof Number) return new JsonPrimitive((Number) node);
        return new JsonPrimitive(String.valueOf(node));
    }

}
