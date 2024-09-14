package com.smokeythebandicoot.witcherycompanion.api.brewing;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.brewing.*;
import net.msrandom.witchery.brewing.action.*;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;


public class BrewRegistry {

    private static final List<CapacityBrewAction> capacityBrews = new ArrayList<>();
    private static final List<UpgradeBrewAction> powerBrews = new ArrayList<>();
    private static final List<UpgradeBrewAction> durationBrews = new ArrayList<>();
    private static final Map<EffectModifierBrewAction.Type, EffectModifierBrewAction> effectModifierBrews = new HashMap<>();
    private static final List<QuaffingSpeedBrewAction> quaffingModifierBrews = new ArrayList<>();
    private static final Map<EnumDyeColor, SetColorBrewAction> colorModifierBrews = new HashMap<>();
    private static final Map<Class<? extends Dispersal>, List<ItemStack>> dispersalBrews = new HashMap<>();


    public static void reloadRegistries() {
        capacityBrews.clear();
        powerBrews.clear();
        durationBrews.clear();
        quaffingModifierBrews.clear();
        colorModifierBrews.clear();
        effectModifierBrews.clear();
        dispersalBrews.clear();

        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {

            // CapacityBrewAction: simply store them, sort at the end by ceiling
            if (action instanceof CapacityBrewAction) {
                capacityBrews.add((CapacityBrewAction) action);
            }

            // DispersalBrewAction: store the class and
            else if (action instanceof DispersalBrewAction) {
                DispersalBrewAction dispersalBrewAction = (DispersalBrewAction) action;
                Dispersal dispersal = dispersalBrewAction.getDispersal();
                ItemStack stack = dispersalBrewAction.getKey().toStack();
                dispersalBrews.computeIfAbsent(dispersal.getClass(), k -> new ArrayList<>()).add(stack);
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

        }

        capacityBrews.sort(Comparator.comparingInt(CapacityBrewAction::getCeiling));
        powerBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
        durationBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
        quaffingModifierBrews.sort(Comparator.comparingInt(QuaffingSpeedBrewAction::getSpeed));
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

    public static SetColorBrewAction getColor(EnumDyeColor color) {
        if (colorModifierBrews.containsKey(color)) {
            return colorModifierBrews.get(color);
        }
        return null;
    }

    public static QuaffingSpeedBrewAction getQuaffing(int index) {
        if (index >= 0 && index < quaffingModifierBrews.size())
            return quaffingModifierBrews.get(index);
        return null;
    }

    public static EffectModifierBrewAction getModifier(EffectModifierBrewAction.Type type) {
        if (effectModifierBrews.containsKey(type)) {
            return effectModifierBrews.get(type);
        }
        return null;
    }

    public static Ingredient getDispersalIngredients(Class<? extends Dispersal> dispersalType) {
        if (dispersalBrews.containsKey(dispersalType)) {
            return Ingredient.fromStacks(dispersalBrews.get(dispersalType).toArray(new ItemStack[]{}));
        }
        return Ingredient.EMPTY;
    }

}
