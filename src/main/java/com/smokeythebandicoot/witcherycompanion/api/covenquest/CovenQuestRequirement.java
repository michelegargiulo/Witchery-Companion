package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import com.smokeythebandicoot.witcherycompanion.api.player.PlayerExtendedDataApi;
import com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.common.CovenWitchData;
import net.msrandom.witchery.common.InfusionPower;
import net.msrandom.witchery.entity.familiar.FamiliarType;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.infusion.Infusion;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.resources.CovenQuestManager;
import net.msrandom.witchery.transformation.CreatureTrait;
import net.msrandom.witchery.transformation.CreatureTraitType;
import net.msrandom.witchery.util.WitcheryUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A condition that a player must meet for a Coven Witch to offer a certain quest. The list of conditions is fixed:
 * there is no custom logic here. A quest has a list of requirements, and the player must meet all of them (an empty list
 * means that the quest has no requirement).
 */
public final class CovenQuestRequirement {

    public enum Type {
        /** The player has an active familiar (of a certain kind, if one is set) */
        FAMILIAR,
        /** The player is infused with a certain infusion (witchery:light, witchery:overworld, witchery:otherwhere or
         * witchery:infernal) and has at least a certain amount of infusion power */
        INFUSION,
        /** The player has at least a certain bottling skill */
        BOTTLING_SKILL,
        /** The player has the Werewolf curse at least at a certain level */
        WEREWOLF,
        /** The player has the Vampire curse at least at a certain level */
        VAMPIRE,
        /** The player has already completed a certain quest (a witch of their coven completed it) */
        PREVIOUS_QUEST,
        /** The player has visited a certain dimension: spirit_world, torment or mirror */
        DIMENSION_VISITED
    }

    /** Dimensions that W:C keeps track of (see ProgressUtils.getDimensionSecret) */
    public static final List<String> TRACKED_DIMENSIONS = Collections.unmodifiableList(Arrays.asList("spirit_world", "torment", "mirror"));

    private final Type type;
    @Nullable private final ResourceLocation id;
    @Nullable private final String name;
    private final int amount;

    private CovenQuestRequirement(Type type, @Nullable ResourceLocation id, @Nullable String name, int amount) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    /** Player has an active familiar */
    public static CovenQuestRequirement anyFamiliar() {
        return new CovenQuestRequirement(Type.FAMILIAR, null, null, 0);
    }

    /** Player has an active familiar of a certain kind, identified by the registry name of its entity (e.g. minecraft:ocelot) */
    public static CovenQuestRequirement familiar(ResourceLocation entityId) {
        return new CovenQuestRequirement(Type.FAMILIAR, entityId, null, 0);
    }

    /** Player is infused with the infusion, whatever the amount of power */
    public static CovenQuestRequirement infusion(ResourceLocation infusionId) {
        return infusion(infusionId, 0);
    }

    /** Player is infused with the infusion and has at least minPower infusion power */
    public static CovenQuestRequirement infusion(ResourceLocation infusionId, int minPower) {
        return new CovenQuestRequirement(Type.INFUSION, infusionId, null, minPower);
    }

    public static CovenQuestRequirement bottlingSkill(int minSkill) {
        return new CovenQuestRequirement(Type.BOTTLING_SKILL, null, null, minSkill);
    }

    public static CovenQuestRequirement werewolf(int minLevel) {
        return new CovenQuestRequirement(Type.WEREWOLF, null, null, minLevel);
    }

    public static CovenQuestRequirement vampire(int minLevel) {
        return new CovenQuestRequirement(Type.VAMPIRE, null, null, minLevel);
    }

    public static CovenQuestRequirement previousQuest(ResourceLocation questId) {
        return new CovenQuestRequirement(Type.PREVIOUS_QUEST, questId, null, 0);
    }

    public static CovenQuestRequirement dimensionVisited(String dimension) {
        return new CovenQuestRequirement(Type.DIMENSION_VISITED, null, dimension, 0);
    }

    /** The requirement of Witchery: the player has an active familiar, of any kind */
    public static List<CovenQuestRequirement> witcheryDefault() {
        List<CovenQuestRequirement> list = new ArrayList<>();
        list.add(anyFamiliar());
        return list;
    }

    public Type getType() { return type; }
    @Nullable public ResourceLocation getId() { return id; }
    @Nullable public String getName() { return name; }
    public int getAmount() { return amount; }

    /** Problems of this requirement (empty if it is valid) */
    public List<String> validate() {
        List<String> problems = new ArrayList<>();
        switch (type) {
            case INFUSION:
                if (id == null) problems.add("infusion requirement without infusion id");
                if (amount < 0) problems.add("infusion requirement with negative power");
                break;
            case PREVIOUS_QUEST:
                if (id == null) problems.add("previous quest requirement without quest id");
                break;
            case DIMENSION_VISITED:
                if (name == null || !TRACKED_DIMENSIONS.contains(name)) {
                    problems.add("dimension '" + name + "' is not tracked, use one of " + TRACKED_DIMENSIONS);
                }
                break;
            case BOTTLING_SKILL:
            case WEREWOLF:
            case VAMPIRE:
                if (amount < 0) problems.add(type + " requirement with a negative amount");
                break;
            default:
                break;
        }
        return problems;
    }

    /** Result of checking a requirement: if it is met, and a short description of what the player has (for the diagnostic
     * command and the logs) */
    public static final class Result {
        private final boolean met;
        private final String detail;

        Result(boolean met, String detail) {
            this.met = met;
            this.detail = detail;
        }

        public boolean isMet() { return met; }
        public String getDetail() { return detail; }
    }

    /** Checks the requirement. Server side only (the data of the player are not available on the client) */
    public boolean isMet(EntityPlayer player) {
        return evaluate(player).isMet();
    }

    /** Checks the requirement and says what the player has. Server side only */
    public Result evaluate(EntityPlayer player) {
        switch (type) {
            case FAMILIAR:
                return evaluateFamiliar(player);
            case INFUSION:
                return evaluateInfusion(player);
            case BOTTLING_SKILL: {
                int skill = PlayerExtendedDataApi.getBottlingSkill(player);
                return new Result(skill >= amount, "bottling skill " + skill);
            }
            case WEREWOLF: {
                int level = getTraitLevel(player, WitcheryCreatureTraits.WEREWOLF);
                return new Result(level >= amount, "werewolf level " + level);
            }
            case VAMPIRE: {
                int level = getTraitLevel(player, WitcheryCreatureTraits.VAMPIRE);
                return new Result(level >= amount, "vampire level " + level);
            }
            case PREVIOUS_QUEST: {
                boolean done = isPreviousQuestMet(player);
                return new Result(done, done ? "quest completed" : "quest not completed");
            }
            case DIMENSION_VISITED: {
                boolean visited = hasProgress(player, ProgressUtils.getDimensionSecret(name));
                return new Result(visited, visited ? "dimension visited" : "dimension not visited");
            }
            default:
                return new Result(true, "nothing to check");
        }
    }

    /** The kind of familiar is the registry key of its FamiliarType (the entity class: ocelot for the cat, toad, owl),
     * as the data files of W:R do (familiar_powers/<namespace>/<entity>.json) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Result evaluateFamiliar(EntityPlayer player) {
        FamiliarType<?, ?> familiarType = Familiars.getActiveFamiliarType(player);
        if (familiarType == null) return new Result(false, "no active familiar");

        Object entityClass = ((net.msrandom.witchery.registry.WitcheryRegistry) FamiliarType.REGISTRY).getKey(familiarType);
        ResourceLocation entityId = entityClass instanceof Class ? EntityList.getKey((Class<? extends Entity>) entityClass) : null;
        String kind = entityId != null ? entityId.toString()
                : (entityClass instanceof Class ? ((Class<?>) entityClass).getName() : "unknown kind");

        if (id == null) return new Result(true, "active familiar: " + kind);
        return new Result(id.equals(entityId), "active familiar: " + kind);
    }

    /** The infusion is identified by its registry key (witchery:light, witchery:overworld, witchery:otherwhere or
     * witchery:infernal) and the power is the current amount of infusion energy (what the medallion sets to 200) */
    private Result evaluateInfusion(EntityPlayer player) {
        InfusionPower power = PlayerExtendedDataApi.getCurrentInfusion(player);
        if (power == null || power.getInfusionType() == null) return new Result(false, "no infusion");

        ResourceLocation current = Infusion.REGISTRY.getKey(power.getInfusionType());
        String detail = "infusion " + (current != null ? current : "none") + ", power " + power.getCurrentPower();
        return new Result(id != null && id.equals(current) && power.getCurrentPower() >= amount, detail);
    }

    private static int getTraitLevel(EntityPlayer player, CreatureTraitType<?> traitType) {
        PlayerExtendedData data = WitcheryUtils.getExtension(player);
        if (data == null) return 0;
        CreatureTrait trait = data.transformations.get(traitType);
        return trait == null ? 0 : trait.getLevel();
    }

    private boolean isPreviousQuestMet(EntityPlayer player) {
        if (id == null) return false;
        PlayerExtendedData data = WitcheryUtils.getExtension(player);
        if (data != null) {
            for (CovenWitchData member : data.coven) {
                CovenQuest quest = member.getQuest();
                if (quest != null && id.equals(CovenQuestManager.INSTANCE.getRegistry().getKey(quest))) return true;
            }
        }
        return hasProgress(player, ProgressUtils.getCovenQuestSecret(id));
    }

    private static boolean hasProgress(EntityPlayer player, String secret) {
        if (secret == null) return false;
        IWitcheryProgress progress = CapabilityWitcheryProgress.getWitcheryProgress(player);
        return progress != null && progress.hasProgress(secret);
    }

    @Override
    public String toString() {
        switch (type) {
            case FAMILIAR: return id == null ? "familiar" : "familiar " + id;
            case INFUSION: return "infusion " + id + " (power >= " + amount + ")";
            case PREVIOUS_QUEST: return "previous quest " + id;
            case DIMENSION_VISITED: return "dimension visited " + name;
            default: return type.name().toLowerCase() + " >= " + amount;
        }
    }

}
