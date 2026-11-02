package com.smokeythebandicoot.witcherycompanion.api.customrite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Plain-data description of a custom Rite. It deliberately contains no Minecraft or Witchery types: its only job is to
 * be converted (see {@link #toRiteJson()}) into the exact JSON format that Witchery: Resurrected reads from
 * data/witchery/rites, so that W:R itself parses sacrifices, circles, conditions and names (and syncs the result
 * to the clients). The JSON is returned as a tree of Map/List/String/Number/Boolean to stay independent from Gson.
 */
public class CustomRiteDefinition {

    /** Rites are loaded by W:R from "<namespace>:<group>/<name>". The group is shared by every custom rite */
    public static final String GROUP = "custom";
    public static final String DEFAULT_NAMESPACE = "witcherycompanion";
    /** Type of the RiteEffect registered in W:R's effect registry. It forwards the rite to the script handler */
    public static final String EFFECT_TYPE = "witcherycompanion:custom_rite";
    /** Type of the ingredient (registered in Forge's CraftingHelper) that resolves to an ingredient held by CustomRiteApi */
    public static final String INGREDIENT_TYPE = "witcherycompanion:custom_rite_ingredient";

    public static final String GLYPH_RITUAL = "witchery:ritual_circle_glyph";
    public static final String GLYPH_INFERNAL = "witchery:infernal_circle_glyph";
    public static final String GLYPH_OTHERWHERE = "witchery:otherwhere_circle_glyph";

    public static class ItemSpec {
        /** Ingredient in the JSON format read by Forge's CraftingHelper. See {@link #ingredientRef(String)} */
        public Object ingredientJson;
        public String identifier = null;
        public boolean optional = false;
        /** Registry id of a RiteAction (witchery:keep, witchery:set_location) or null */
        public String action = null;
        /** null = item is consumed. Otherwise a "returns" object ready to be serialized */
        public Map<String, Object> returns = null;

        public ItemSpec(Object ingredientJson) {
            this.ingredientJson = ingredientJson;
        }
    }

    public static class CircleSpec {
        public final String glyph;
        public final int size;

        public CircleSpec(String glyph, int size) {
            this.glyph = glyph;
            this.size = size;
        }
    }

    public static class ConditionSpec {
        public final String type;
        public final boolean negate;

        public ConditionSpec(String type, boolean negate) {
            this.type = type;
            this.negate = negate;
        }
    }

    private final String id;
    private String name;
    private String description = "";
    private float power = 0f;
    private int covenSize = 0;
    private boolean hidden = false;
    private String insertAfter = null;
    private String section = null;
    private final List<ItemSpec> items = new ArrayList<>();
    private final List<String> entities = new ArrayList<>();
    private final List<CircleSpec> circles = new ArrayList<>();
    private final List<ConditionSpec> conditions = new ArrayList<>();

    public CustomRiteDefinition(String id) {
        this.id = normalizeId(id);
        this.name = this.id.substring(this.id.indexOf(':') + 1);
    }

    /** Key of the Nth item of a rite in CustomRiteApi's ingredient table */
    public static String ingredientKey(String riteId, int index) {
        return riteId + "#" + index;
    }

    /** JSON of an ingredient that is not described by data but held by CustomRiteApi under the given key */
    public static Map<String, Object> ingredientRef(String key) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("type", INGREDIENT_TYPE);
        json.put("key", key);
        return json;
    }

    /** Lowercases the id and adds the default namespace if missing. Throws IllegalArgumentException if invalid */
    public static String normalizeId(String rawId) {
        if (rawId == null) throw new IllegalArgumentException("Rite id is null");
        String trimmed = rawId.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty()) throw new IllegalArgumentException("Rite id is empty");
        String namespace = DEFAULT_NAMESPACE;
        String path = trimmed;
        int colon = trimmed.indexOf(':');
        if (colon >= 0) {
            namespace = trimmed.substring(0, colon);
            path = trimmed.substring(colon + 1);
        }
        if (!namespace.matches("[a-z0-9_.-]+"))
            throw new IllegalArgumentException("Invalid namespace in rite id '" + rawId + "' (allowed: a-z 0-9 _ . -)");
        // No '/' in the path: W:R splits "<group>/<name>" on the first slash
        if (!path.matches("[a-z0-9_.-]+"))
            throw new IllegalArgumentException("Invalid path in rite id '" + rawId + "' (allowed: a-z 0-9 _ . - and no '/')");
        return namespace + ":" + path;
    }

    /** Id of an existing rite: lower case, with "witchery" as the default namespace */
    public static String normalizeReference(String rawId) {
        if (rawId == null) throw new IllegalArgumentException("Rite id is null");
        String trimmed = rawId.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty()) throw new IllegalArgumentException("Rite id is empty");
        return trimmed.indexOf(':') >= 0 ? trimmed : "witchery:" + trimmed;
    }

    /** Group of the books ("infusion" or "witchery:infusion"): lower case, with "witchery" as the default namespace */
    public static String normalizeSection(String rawSection) {
        return normalizeReference(rawSection);
    }

    /** Expands the short names of the glyphs (ritual, infernal, otherwhere). Other values are returned untouched */
    public static String normalizeGlyph(String glyph) {
        if (glyph == null) return null;
        switch (glyph.trim().toLowerCase(Locale.ROOT)) {
            case "ritual": case "golden": case "chalk": return GLYPH_RITUAL;
            case "infernal": return GLYPH_INFERNAL;
            case "otherwhere": return GLYPH_OTHERWHERE;
            default: return glyph.trim();
        }
    }

    /** Expands the short names of W:R's conditions (night, thunder, overworld) */
    public static String normalizeCondition(String condition) {
        if (condition == null) return null;
        String c = condition.trim();
        return c.indexOf(':') >= 0 ? c : "witchery:" + c;
    }

    /**
     * Rite names and descriptions go through I18n.format(), so a '%' would be handled as a format specifier.
     * The text is used as a translation key by W:R: if no translation exists, the key itself is displayed.
     */
    public static String escapeForLang(String text) {
        return text == null ? "" : text.replace("%", "%%");
    }

    // ------------------------------------------------------------------------------------------ Mutators

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public float getPower() { return power; }
    public int getCovenSize() { return covenSize; }
    public boolean isHidden() { return hidden; }
    public List<ItemSpec> getItems() { return items; }
    public List<String> getEntities() { return entities; }
    public List<CircleSpec> getCircles() { return circles; }
    public List<ConditionSpec> getConditions() { return conditions; }

    /** Path of the rite id, without namespace */
    public String getPath() { return id.substring(id.indexOf(':') + 1); }
    public String getNamespace() { return id.substring(0, id.indexOf(':')); }

    /** Key under which the rite is fed to W:R's RiteManager: "<namespace>:custom/<path>" */
    public String getDataKey() { return getNamespace() + ":" + GROUP + "/" + getPath(); }

    /** Id of the rite after which this rite is placed in the books, or null */
    public String getInsertAfter() { return insertAfter; }
    /** Section (group) of the books where this rite is listed, or null */
    public String getSection() { return section; }
    public void setInsertAfter(String riteId) { this.insertAfter = riteId == null ? null : normalizeReference(riteId); }
    public void setSection(String section) { this.section = section == null ? null : normalizeSection(section); }

    public void setName(String name) { this.name = name == null ? "" : name; }
    public void setDescription(String description) { this.description = description == null ? "" : description; }
    public void setPower(float power) { this.power = power; }
    public void setCovenSize(int covenSize) { this.covenSize = covenSize; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }

    /** Builds a "returns" object: witchery:self */
    public static Map<String, Object> returnsSelf() {
        Map<String, Object> returns = new LinkedHashMap<>();
        returns.put("type", "witchery:self");
        return returns;
    }

    /** Builds a "returns" object: witchery:copy_data. Data (metadata) can be null */
    public static Map<String, Object> returnsCopyData(String itemId, Integer data) {
        Map<String, Object> returns = new LinkedHashMap<>();
        returns.put("type", "witchery:copy_data");
        returns.put("item", itemId);
        if (data != null) returns.put("data", data);
        return returns;
    }

    /** Returns a list of problems that would make the rite unusable. Empty list = valid */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        if (items.isEmpty() && entities.isEmpty() && power <= 0f)
            problems.add("the rite has no sacrifices (add at least an item, an entity or altar power)");
        if (circles.isEmpty())
            problems.add("the rite has no circles (add at least one with addCircle)");
        for (CircleSpec circle : circles) {
            if (circle.glyph == null || circle.glyph.indexOf(':') < 0)
                problems.add("invalid glyph '" + circle.glyph + "' (use ritual, infernal, otherwhere or a block id)");
            if (circle.size < 1)
                problems.add("invalid circle size " + circle.size + " (W:R uses 1, 2 or 3)");
        }
        if (covenSize < 0) problems.add("negative coven size");
        if (power < 0f) problems.add("negative altar power");
        return problems;
    }

    // ------------------------------------------------------------------------------------------ JSON

    /** Converts the definition into the JSON tree that W:R's RiteManager reads for a rite, without neighbours */
    public Map<String, Object> toRiteJson() {
        return toRiteJson(null, null);
    }

    /**
     * Converts the definition into the JSON tree that W:R's RiteManager reads for a rite
     * @param left id of the rite that comes before this one in the books, or null
     * @param right id of the rite that comes after this one in the books, or null
     */
    public Map<String, Object> toRiteJson(String left, String right) {
        Map<String, Object> root = new LinkedHashMap<>();

        Map<String, Object> sacrifices = new LinkedHashMap<>();
        // "items" is always written, even if empty: W:R's item serializer does not tolerate a missing key (NPE), while
        // the serializers of entities and power do
        List<Object> itemList = new ArrayList<>();
        for (ItemSpec item : items) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("ingredient", item.ingredientJson);
            if (item.identifier != null && !item.identifier.isEmpty()) entry.put("identifier", item.identifier);
            if (item.returns != null) entry.put("returns", item.returns);
            if (item.action != null) entry.put("action", item.action);
            if (item.optional) entry.put("optional", Boolean.TRUE);
            itemList.add(entry);
        }
        sacrifices.put("items", itemList);
        if (!entities.isEmpty()) sacrifices.put("entities", new ArrayList<Object>(entities));
        if (power > 0f) sacrifices.put("power", (double) power);
        root.put("sacrifices", sacrifices);

        List<Object> circleList = new ArrayList<>();
        for (CircleSpec circle : circles) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("glyph", circle.glyph);
            entry.put("size", circle.size);
            circleList.add(entry);
        }
        root.put("circles", circleList);

        if (!conditions.isEmpty()) {
            List<Object> conditionList = new ArrayList<>();
            for (ConditionSpec condition : conditions) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("type", condition.type);
                if (condition.negate) entry.put("negate", Boolean.TRUE);
                conditionList.add(entry);
            }
            root.put("conditions", conditionList);
        }

        if (left != null || right != null) {
            Map<String, Object> sorting = new LinkedHashMap<>();
            if (left != null) sorting.put("left", left);
            if (right != null) sorting.put("right", right);
            root.put("sorting", sorting);
        }

        root.put("name_type", "custom");
        root.put("name_key", escapeForLang(name));
        root.put("description_type", "custom");
        root.put("description_key", escapeForLang(description));
        if (hidden) root.put("hidden", Boolean.TRUE);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", EFFECT_TYPE);
        result.put("rite", id);
        root.put("result", result);
        return root;
    }
}
