package com.smokeythebandicoot.witcherycompanion.api.brewing;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.*;
import net.msrandom.witchery.brewing.action.*;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import net.msrandom.witchery.brewing.action.effect.BrewEffect;
import net.msrandom.witchery.resources.BrewActionManager;

import java.util.*;
import java.util.stream.Collectors;


public class BrewRegistry {

    private static final List<CapacityBrewAction> capacityBrews = new ArrayList<>();
    private static final List<UpgradeBrewAction> powerBrews = new ArrayList<>();
    private static final List<UpgradeBrewAction> durationBrews = new ArrayList<>();
    private static final Map<EffectModifierBrewAction.Type, EffectModifierBrewAction> effectModifierBrews = new HashMap<>();
    private static final List<QuaffingSpeedBrewAction> quaffingModifierBrews = new ArrayList<>();
    private static final Map<EnumDyeColor, SetColorBrewAction> colorModifierBrews = new HashMap<>();
    private static final Map<Class<? extends Dispersal>, List<DispersalBrewAction>> dispersalBrews = new HashMap<>();
    private static final List<IncrementBrewAction> extentBrews = new ArrayList<>();
    private static final List<IncrementBrewAction> lingeringBrews = new ArrayList<>();
    private static final Map<Integer, List<BrewEffect>> effectBrews = new HashMap<>();


    public static void reloadRegistries() {
        capacityBrews.clear();
        powerBrews.clear();
        durationBrews.clear();
        quaffingModifierBrews.clear();
        colorModifierBrews.clear();
        effectModifierBrews.clear();
        dispersalBrews.clear();
        extentBrews.clear();
        lingeringBrews.clear();
        effectBrews.clear();

        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {

            // CapacityBrewAction: simply store them, sort at the end by ceiling
            if (action instanceof CapacityBrewAction) {
                capacityBrews.add((CapacityBrewAction) action);
            }

            // DispersalBrewAction: store the class and
            else if (action instanceof DispersalBrewAction) {
                DispersalBrewAction dispersalBrewAction = (DispersalBrewAction) action;
                Dispersal dispersal = dispersalBrewAction.getDispersal();
                dispersalBrews.computeIfAbsent(dispersal.getClass(), k -> new ArrayList<>()).add(dispersalBrewAction);
            }

            else if (action instanceof UpgradeBrewAction && action instanceof IUpgradeBrewActionAccessor) {
                UpgradeBrewAction upgradeBrewAction = (UpgradeBrewAction) action;
                IUpgradeBrewActionAccessor accessor = (IUpgradeBrewActionAccessor) action;
                if (accessor.increasesPower()) {
                    powerBrews.add((UpgradeBrewAction) action);
                } else {
                    durationBrews.add((UpgradeBrewAction) action);
                }
            }

            else if (action instanceof SetColorBrewAction) {
                SetColorBrewAction colorModifierAction = (SetColorBrewAction) action;
                colorModifierBrews.put(action.getForcedColor(), colorModifierAction);
            }

            else if (action instanceof QuaffingSpeedBrewAction) {
                QuaffingSpeedBrewAction quaffingModifierAction = (QuaffingSpeedBrewAction) action;
                quaffingModifierBrews.add(quaffingModifierAction);
            }

            else if (action instanceof EffectModifierBrewAction) {
                EffectModifierBrewAction effectModifierAction = (EffectModifierBrewAction) action;
                effectModifierBrews.put(effectModifierAction.getType(), effectModifierAction);
            }

            else if (action instanceof IncrementBrewAction && action instanceof IIncrementBrewActionAccessor) {
                IncrementBrewAction incrementBrewAction = (IncrementBrewAction) action;
                IIncrementBrewActionAccessor accessor = (IIncrementBrewActionAccessor) action;
                if (accessor.increasesExtent()) {
                    extentBrews.add((IncrementBrewAction) action);
                } else {
                    lingeringBrews.add((IncrementBrewAction) action);
                }
            }

            else if (action instanceof EffectBrewAction) {
                EffectBrewAction effectBrewAction = (EffectBrewAction) action;
                BrewEffect effect = effectBrewAction.getEffect();
                int level = effectBrewAction.getEffectLevel();
                effectBrews.computeIfAbsent(level, k -> new ArrayList<>()).add(effect);
            }

        }

        capacityBrews.sort(Comparator.comparingInt(CapacityBrewAction::getCeiling));
        powerBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
        durationBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
        quaffingModifierBrews.sort(Comparator.comparingInt(QuaffingSpeedBrewAction::getSpeed));
        extentBrews.sort(Comparator.comparingInt(IncrementBrewAction::getLimit));
        lingeringBrews.sort(Comparator.comparingInt(IncrementBrewAction::getLimit));
    }


    public static CapacityBrewAction getCapacity(int index) {
        if (index >= 0 && index < capacityBrews.size())
            return capacityBrews.get(index);
        return null;
    }

    public static UpgradeBrewAction getPower(int index) {
        if (index >= 0 && index < powerBrews.size())
            return powerBrews.get(index);
        return null;
    }

    public static UpgradeBrewAction getDuration(int index) {
        if (index >= 0 && index < durationBrews.size())
            return durationBrews.get(index);
        return null;
    }

    public static SetColorBrewAction getColorBrew(EnumDyeColor color) {
        if (colorModifierBrews.containsKey(color)) {
            return colorModifierBrews.get(color);
        }
        return null;
    }

    public static Map<EnumDyeColor, SetColorBrewAction> getColorBrews() {
        return new HashMap<>(colorModifierBrews);
    }

    public static QuaffingSpeedBrewAction getQuaffingBrew(int index) {
        if (index >= 0 && index < quaffingModifierBrews.size())
            return quaffingModifierBrews.get(index);
        return null;
    }

    public static List<QuaffingSpeedBrewAction> getQuaffingBrews() {
        return new ArrayList<>(quaffingModifierBrews);
    }

    public static EffectModifierBrewAction getModifier(EffectModifierBrewAction.Type type) {
        if (effectModifierBrews.containsKey(type)) {
            return effectModifierBrews.get(type);
        }
        return null;
    }

    public static Map<EffectModifierBrewAction.Type, EffectModifierBrewAction> getModifiers() {
        return new HashMap<>(effectModifierBrews);
    }

    public static List<DispersalBrewAction> getDispersalBrews(Class<? extends Dispersal> dispersalType) {
        if (dispersalBrews.containsKey(dispersalType)) {
            return new ArrayList<>(dispersalBrews.get(dispersalType));
        }
        return null;
    }

    public static Ingredient getDispersalIngredients(Class<? extends Dispersal> dispersalType) {
        if (dispersalBrews.containsKey(dispersalType)) {
            return Ingredient.fromStacks(dispersalBrews.get(dispersalType).stream()
                    .map(dispersalBrewAction -> dispersalBrewAction.getKey().toStack())
                    .collect(Collectors.toList())
                    .toArray(new ItemStack[]{}));
        }
        return Ingredient.EMPTY;
    }

    public static IncrementBrewAction getExtent(int index) {
        if (index >= 0 && index < extentBrews.size())
            return extentBrews.get(index);
        return null;
    }

    public static IncrementBrewAction getLingering(int index) {
        if (index >= 0 && index < lingeringBrews.size())
            return lingeringBrews.get(index);
        return null;
    }

    public static HashMap<Integer, List<BrewEffect>> getEffects() {
        return new HashMap<>(effectBrews);
    }

}
