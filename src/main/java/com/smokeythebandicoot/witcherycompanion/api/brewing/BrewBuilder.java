package com.smokeythebandicoot.witcherycompanion.api.brewing;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.*;
import net.msrandom.witchery.brewing.action.*;
import net.msrandom.witchery.resources.BrewActionManager;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BrewBuilder {

    private final List<EffectBrewActionInfo> effects;
    private int capacity = -1;
    private int quaffingBonus = 0;
    private final HashMap<EffectModifierBrewAction.Type, Boolean> modifiers;
    private EnumDyeColor color = null;
    private EDispersalType dispersal = EDispersalType.DRINK;
    private EDispersalModifierLingering lingering = EDispersalModifierLingering.NONE;
    private EDispersalModifierExtent extent = EDispersalModifierExtent.NONE;


    private BrewBuilder() {
        effects = new ArrayList<>();
        modifiers = new HashMap<>();
        for (EffectModifierBrewAction.Type val : EffectModifierBrewAction.Type.values()) {
            modifiers.put(val, false);
        }
    }


    /** =============== EFFECTS =============== **/

    public BrewBuilder withEffect(EffectBrewAction action) {
        if (action != null) {
            effects.add(new EffectBrewActionInfo(action, 0, 0));
        }
        return this;
    }

    public BrewBuilder withEffect(EffectBrewAction action, EBrewActionPower power) {
        if (action != null) {
            effects.add(new EffectBrewActionInfo(action, power.ordinal(), 0));
        }
        return this;
    }

    public BrewBuilder withEffect(EffectBrewAction action, EBrewActionDuration duration) {
        if (action != null) {
            effects.add(new EffectBrewActionInfo(action, 0, duration.ordinal()));
        }
        return this;
    }

    public BrewBuilder withEffect(EffectBrewAction action, EBrewActionPower power, EBrewActionDuration duration) {
        if (action != null) {
            effects.add(new EffectBrewActionInfo(action, power.ordinal(), duration.ordinal()));
        }
        return this;
    }


    /** =============== CAPACITY =============== **/

    public BrewBuilder withCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public BrewBuilder withMinimumCapacity(int capacity) {
        this.capacity = -1;
        return this;
    }

    public boolean hasEnoughCapacity() {
        return this.capacity == -1 || getRemainingCapacity() >= 0;
    }

    public int getCurrentUsedCapacity() {
        int usedCapacity = 0;
        for (EffectBrewActionInfo effect : this.effects) {
            usedCapacity += effect.getRequiredCapacity();
        }
        return usedCapacity;
    }

    public int getRemainingCapacity() {
        // Compare against maximum possible capacity
        if (this.capacity == -1) {
            return BrewRegistry.getMaximumPossibleCapacity() - getCurrentUsedCapacity();
        }
        // Compare against set capacity. Can be negative
        else {
            return this.capacity - getCurrentUsedCapacity();
        }
    }


    /** =============== MODIFIERS =============== **/

    public BrewBuilder withEffectModifiers(int quaffingSpeedBonus) {
        this.quaffingBonus = quaffingSpeedBonus;
        return this;
    }

    public BrewBuilder withEffectModifiers(EnumDyeColor color) {
        this.color = color;
        return this;
    }

    public BrewBuilder withEffectModifiers(int quaffingSpeedBonus, EnumDyeColor color) {
        this.quaffingBonus = quaffingSpeedBonus;
        this.color = color;
        return this;
    }

    public BrewBuilder withGeneralModifier(EffectModifierBrewAction.Type modifier, boolean enabled) {
        this.modifiers.put(modifier, enabled);
        return this;
    }

    public BrewBuilder withDispersal(EDispersalType dispersal) {
        this.dispersal = dispersal;
        return this;
    }

    public BrewBuilder withDispersal(EDispersalType dispersal, EDispersalModifierExtent extent) {
        this.dispersal = dispersal;
        this.extent = extent;
        return this;
    }

    public BrewBuilder withDispersal(EDispersalType dispersal, EDispersalModifierLingering lingering) {
        this.dispersal = dispersal;
        this.lingering = lingering;
        return this;
    }

    public BrewBuilder withDispersal(EDispersalType dispersal, EDispersalModifierExtent extent, EDispersalModifierLingering lingering) {
        this.dispersal = dispersal;
        this.lingering = lingering;
        this.extent = extent;
        return this;
    }


    /** =============== BUILDING =============== **/

    public static BrewBuilder create() {
        return new BrewBuilder();
    }

    public List<ItemStack> brew(boolean asRitual) {
        //TODO: Check for Power Ceiling to see if it requires Nether Star
        // It needs to be done after having established the required capacity.
        // If it exceeds 7 (check code!), it needs Nether Star

        // Capacity
        int requiredCapacity = getCurrentUsedCapacity();
        List<ItemStack> capacityStacks = getCapacityItems(this.capacity == -1 ? requiredCapacity : this.capacity);
        if (capacityStacks == null) {
            return null; // Cannot build brew due to capacity
        }
        List<ItemStack> result = new ArrayList<>(capacityStacks);

        // Quaffing
        int currentQuaffingBonus = 0;
        int index = 0;
        while (currentQuaffingBonus < this.quaffingBonus) {
            QuaffingSpeedBrewAction action = BrewRegistry.getQuaffingBrew(index);
            if (action == null) {
                return null; // Cannot build brew due to quaffing
            }
            currentQuaffingBonus += action.getDrinkSpeedModifiers();
            result.add(action.getKey().toStack());
        }

        // Color
        if (this.color != null) {
            SetColorBrewAction action = BrewRegistry.getColorBrew(this.color);
            if (action == null) {
                return null; // Cannot build brew due to color
            }
            result.add(action.getKey().toStack());
        }

        // General Brew Modifiers
        for (EffectModifierBrewAction.Type type : this.modifiers.keySet()) {
            if (this.modifiers.get(type)) {
                EffectModifierBrewAction action = BrewRegistry.getModifier(type);
                if (action == null) {
                    return null; // Cannot build brew due to general modifier
                }
                result.add(action.getKey().toStack());
            }
        }

        // Effects
        for (EffectBrewActionInfo effect : this.effects) {
            List<ItemStack> powerStacks = getItemsFor(
                    effect.power,
                    BrewRegistry::getPower,
                    UpgradeBrewAction::getLimit
            );
            if (powerStacks == null) {
                return null; // Cannot build brew due to power of this effect
            }
            result.addAll(powerStacks);

            List<ItemStack> durationStacks = getItemsFor(
                    effect.duration,
                    BrewRegistry::getDuration,
                    UpgradeBrewAction::getLimit
            );
            if (durationStacks == null) {
                return null; // Cannot build brew due to duration of this effect
            }
            result.addAll(durationStacks);

            result.add(effect.effect.getKey().toStack());
        }

        // Dispersal
        if (this.dispersal != EDispersalType.DRINK) {
            Class<? extends Dispersal> dispersalClass = this.dispersal.clazz;
            List<DispersalBrewAction> actions = BrewRegistry.getDispersalBrews(dispersalClass);
            if (actions == null || actions.isEmpty()) {
                return null; // Cannot build brew due to dispersal
            }
            result.add(actions.get(0).getKey().toStack());

            // Dispersal Modifiers
            // Extent
            if (this.extent != EDispersalModifierExtent.NONE) {
                List<ItemStack> extentStacks = getItemsFor(
                        this.extent.ordinal(), // Extent goes from 1 to 3
                        BrewRegistry::getExtent,
                        IncrementBrewAction::getLimit
                );
                if (extentStacks == null) {
                    return null; // Cannot build brew due to extent
                }
                result.addAll(extentStacks);
            }

            // Lingering
            if (this.lingering != EDispersalModifierLingering.NONE) {
                List<ItemStack> lingeringStacks = getItemsFor(
                        this.lingering.ordinal(), // Extent goes from 1 to 3
                        BrewRegistry::getLingering,
                        IncrementBrewAction::getLimit
                );
                if (lingeringStacks == null) {
                    return null; // Cannot build brew due to lingering
                }
                result.addAll(lingeringStacks);
            }
        }

        return result;
    }


    /** =============== MISC =============== **/

    public static int getAltarPowerCost(List<ItemStack> stacks, boolean asRitual) {
        float currentPower = 0.0f;
        for (ItemStack stack : stacks) {
            BrewAction action = BrewActionManager.INSTANCE.getAction(ItemKey.fromStack(stack));
            if (action != null) {
                currentPower += action.getPowerCost();
            }
        }
        return (int)(asRitual ? currentPower * 1.4f : currentPower);
    }


    /** =============== INTERNAL =============== **/

    private List<ItemStack> getCapacityItems(int requiredCapacity) {
        List<CapacityBrewAction> capacityActions = new ArrayList<>();
        int currentCapacity = 0;
        boolean ceilingRemoved = false;

        // Power Ceiling calculations
        CapacityBrewAction ceilingRemover = BrewRegistry.getLowestCapacityCeilingRemover();
        if (requiredCapacity >= ModConfig.PatchesConfiguration.BrewsTweaks.common_tweakCustomPowerCeiling) {
            if (ceilingRemover == null) {
                return null; // Cannot build brew: brew requires ceiling to be disabled, but there's no ceiling remover
            }
            capacityActions.add(ceilingRemover);
            currentCapacity += ceilingRemover.getIncrement();
            ceilingRemoved = true;
        }

        // Remaining Capacity
        int index = 0;
        while (currentCapacity < requiredCapacity) {
            CapacityBrewAction capacityBrewAction = BrewRegistry.getCapacity(index);
            if (capacityBrewAction == null) {
                return null; // Cannot build brew: not enough capacity even if using all capacity items
            }
            // Skip ceiling remover (if previously added)
            if (ceilingRemoved && capacityBrewAction.getKey() == ceilingRemover.getKey()) {
                index++;
                continue;
            }
            currentCapacity += capacityBrewAction.getIncrement();
            capacityActions.add(capacityBrewAction);
        }

        // Sort by ceiling and map to ItemStacks
        capacityActions.sort(Comparator.comparingInt(CapacityBrewAction::getCeiling));
        return capacityActions.stream()
                .map(action -> action.getKey().toStack())
                .collect(Collectors.toList());
    }

    private <T extends BrewAction> List<ItemStack> getItemsFor(int target, Function<Integer, T> brewActionSupplier, Function<T, Integer> extractor) {
        List<ItemStack> result = new ArrayList<>();
        int current = 0;
        int index = 0;

        while (current < target) {
            T brew = brewActionSupplier.apply(index);
            if (brew == null) {
                return null;
            }
            current += extractor.apply(brew);
            result.add(brew.getKey().toStack());
        }
        return result;
    }


    private static class EffectBrewActionInfo {

        private final int power;
        private final int duration;
        private final EffectBrewAction effect;

        private EffectBrewActionInfo(EffectBrewAction effect, int power, int duration) {
            this.power = power;
            this.duration = duration;
            this.effect = effect;
        }

        private int getRequiredCapacity() {
            if (effect == null) {
                return 0;
            }
            return effect.getEffectLevel();
        }

    }


    /** =============== ENUMS =============== **/

    public enum EBrewActionPower {
        I,
        II,
        III,
        IV
    }

    public enum EBrewActionDuration {
        I,
        II,
        III,
        IV
    }

    public enum EDispersalType {
        DRINK(null),
        INSTANT(InstantDispersal.class),
        GAS(GasDispersal.class),
        LIQUID(LiquidDispersal.class),
        TRIGGERED(TriggeredDispersal.class),
        ;

        final Class<? extends Dispersal> clazz;

        EDispersalType(Class<? extends Dispersal> clazz) {
            this.clazz = clazz;
        }

    }

    public enum EDispersalModifierExtent {
        NONE,
        I,
        II,
        III
    }

    public enum EDispersalModifierLingering {
        NONE,
        I,
        II,
        III
    }

}
