package com.smokeythebandicoot.witcherycompanion.api.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.brewing.Dispersal;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.brewing.action.DispersalBrewAction;
import net.msrandom.witchery.brewing.action.UpgradeBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;


public class BrewRegistry {

    private static List<CapacityBrewAction> capacityBrews = new ArrayList<>();
    private static List<UpgradeBrewAction> powerBrews = new ArrayList<>();
    private static List<UpgradeBrewAction> durationBrews = new ArrayList<>();
    private static Map<Class<? extends Dispersal>, List<ItemStack>> dispersalBrews = new HashMap<>();


    public static void reloadRegistries() {
        capacityBrews = new ArrayList<>();
        powerBrews = new ArrayList<>();
        durationBrews = new ArrayList<>();
        dispersalBrews = new HashMap<>();

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

        }

        capacityBrews.sort(Comparator.comparingInt(CapacityBrewAction::getCeiling));
        powerBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
        durationBrews.sort(Comparator.comparingInt(UpgradeBrewAction::getLimit));
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


    public static Ingredient getDispersalIngredients(Class<? extends Dispersal> dispersal) {
        if (dispersalBrews.containsKey(dispersal)) {
            return Ingredient.fromStacks(dispersalBrews.get(dispersal).toArray(new ItemStack[]{}));
        }
        return Ingredient.EMPTY;
    }
}
