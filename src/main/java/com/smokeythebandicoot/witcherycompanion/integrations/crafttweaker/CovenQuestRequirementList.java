package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestRequirement;
import crafttweaker.CraftTweakerAPI;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Requirements being defined by a builder. Not exposed to ZenScript. Until the script adds one (or says noRequirements) the
 * list is "not set", and the quest keeps the requirement of Witchery (an active familiar). The first requirement added starts
 * a new list: it does not add to the familiar of Witchery.
 */
final class CovenQuestRequirementList {

    private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");

    /** Null while the script did not touch the requirements */
    private List<CovenQuestRequirement> requirements = null;

    /** Where the errors are written, for example "Custom coven quest witcherycompanion:foo" */
    private final String owner;

    CovenQuestRequirementList(String owner) {
        this.owner = owner;
    }

    boolean isSet() { return requirements != null; }

    List<CovenQuestRequirement> get() { return requirements; }

    void clear() {
        requirements = new ArrayList<>();
    }

    private void add(CovenQuestRequirement requirement) {
        if (requirements == null) requirements = new ArrayList<>();
        requirements.add(requirement);
    }

    void addFamiliar(String entityId) {
        if (entityId == null) {
            add(CovenQuestRequirement.anyFamiliar());
            return;
        }
        ResourceLocation id = parse(entityId, "minecraft", "requireFamiliar");
        if (id != null) add(CovenQuestRequirement.familiar(id));
    }

    /** Accepts the short names light, earth (or overworld), infernal and otherwhere, or a full infusion id */
    void addInfusion(String infusionId, int minPower) {
        String name = infusionId == null ? "" : infusionId.trim().toLowerCase();
        if (name.equals("earth")) name = "overworld";
        ResourceLocation id = parse(name, "witchery", "requireInfusion");
        if (id != null) add(CovenQuestRequirement.infusion(id, minPower));
    }

    void addBottlingSkill(int minSkill) {
        add(CovenQuestRequirement.bottlingSkill(minSkill));
    }

    void addWerewolf(int minLevel) {
        add(CovenQuestRequirement.werewolf(minLevel));
    }

    void addVampire(int minLevel) {
        add(CovenQuestRequirement.vampire(minLevel));
    }

    void addPreviousQuest(String questId) {
        ResourceLocation id = parseQuestId(questId, "requirePreviousQuest");
        if (id != null) add(CovenQuestRequirement.previousQuest(id));
    }

    void addDimensionVisited(String dimension) {
        add(CovenQuestRequirement.dimensionVisited(dimension == null ? null : dimension.trim().toLowerCase()));
    }

    /** Reference to a quest of W:R or a custom one: the namespace defaults to witchery */
    ResourceLocation parseQuestId(String raw, String method) {
        return parse(raw, "witchery", method);
    }

    private ResourceLocation parse(String raw, String defaultNamespace, String method) {
        ResourceLocation id = parseId(raw, defaultNamespace);
        if (id == null) {
            CraftTweakerAPI.logError(owner + ": " + method + ": '" + raw + "' is not a valid id (namespace:path, with a-z 0-9 _ . - only)");
        }
        return id;
    }

    /** Parses "namespace:path" (or "path", with the default namespace). Null if it is not valid */
    static ResourceLocation parseId(String raw, String defaultNamespace) {
        if (raw == null) return null;
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;

        String namespace = defaultNamespace;
        String path = trimmed;
        int colon = trimmed.indexOf(':');
        if (colon >= 0) {
            namespace = trimmed.substring(0, colon);
            path = trimmed.substring(colon + 1);
        }
        if (!NAMESPACE.matcher(namespace).matches() || !PATH.matcher(path).matches()) return null;
        return new ResourceLocation(namespace, path);
    }

}
