package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.brewing.ICapacityBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.IPatchouliSerializable;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ProgressionProcessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;


public class CapacityBrewActionProcessor extends ProgressionProcessor implements IComponentProcessor {

    private static List<CapacityBrewActionInfo> capacityBrews = null;


    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        if (capacityBrews == null || capacityBrews.isEmpty()) {
            updateCapacityMap();
        }
    }

    @Override
    public String process(String key) {

        // Convert the set into a sorted list accessible by index
        int index = ProcessorUtils.getIndexFromKey(key, "capacity_brew_item");

        if (index > -1 && index < capacityBrews.size()) {
            CapacityBrewActionInfo info = capacityBrews.get(index);

            // Checks if it is secret and if player has knowledge about it
            if (shouldHide(info))
                return null;

            return info.serialize();

        }
        WitcheryCompanion.logger.warn("Could not parse key for CauldronCapacityProcessor. Key outside of bounds. " +
                "Key: {}, Capacity Brew Size: {}", key, capacityBrews == null ? "null" : capacityBrews.size());
        return null;
    }

    private static void updateCapacityMap() {
        SortedSet<CapacityBrewActionInfo> sortedBrews = new TreeSet<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof CapacityBrewAction) {
                CapacityBrewAction capacityBrewAction = (CapacityBrewAction)action;
                sortedBrews.add(new CapacityBrewActionInfo(capacityBrewAction));
            }
        }
        capacityBrews = new ArrayList<>(sortedBrews);
    }

    /** Returns an ordered list of itemstacks to throw in the cauldron to have the required capacity */
    public static List<Ingredient> getItemsForCapacity(int requiredCapacity) {
        List<Ingredient> ingredients = new ArrayList<>();

        // Too early, return empty list
        if (capacityBrews == null || capacityBrews.isEmpty()) {
            return ingredients;
        }

        // Build the list
        int currentCapacity = 0;
        for (CapacityBrewActionInfo capacityBrewInfo : capacityBrews) {
            if (currentCapacity >= requiredCapacity) {
                break;
            }
            currentCapacity += capacityBrewInfo.increment;
            ingredients.add(Ingredient.fromStacks(capacityBrewInfo.stack));
        }
        return ingredients;
    }


    public static class CapacityBrewActionInfo implements Comparable<CapacityBrewActionInfo>, IPatchouliSerializable, ISecretInfo {

        public ItemStack stack;
        public int increment;
        public boolean removesCeiling;
        public boolean secret;

        public CapacityBrewActionInfo() { }

        public CapacityBrewActionInfo(CapacityBrewAction action) {
            this.stack = action.getKey().toStack();
            this.increment = action.getIncrement();
            this.secret = action.getHidden();
            if ((Object)action instanceof ICapacityBrewActionAccessor) {
                ICapacityBrewActionAccessor accessor = (ICapacityBrewActionAccessor)(Object) action;
                this.removesCeiling = accessor.getRemoveCeiling();
            } else {
                this.removesCeiling = false;
            }
        }

        public CapacityBrewActionInfo(ItemStack stack, int increment, boolean removesCeiling, boolean secret) {
            this.stack = stack;
            this.increment = increment;
            this.removesCeiling = removesCeiling;
            this.secret = secret;
        }


        @Override
        public int compareTo(CapacityBrewActionInfo o) {
            if (o == null) return 1;
            if (increment != o.increment)
                return Integer.compare(increment, o.increment);
            return stack.getItem().getRegistryName().compareTo(o.stack.getItem().getRegistryName());
        }

        // Format:
        // <stack>,<increment>,<removesCeiling>,<secret>
        @Override
        public String serialize() {
            StringBuilder sb = new StringBuilder();
            sb.append(ItemStackUtil.serializeStack(stack))
                    .append(",")
                    .append(increment)
                    .append(",")
                    .append(removesCeiling)
                    .append(",")
                    .append(secret);
            return sb.toString();
        }

        @Override
        public void deserialize(String str) {
            String[] splits = str.split(",");
            if (splits.length != 4) return;
            try {
                this.stack = ItemStackUtil.loadStackFromString(splits[0]);
                this.increment = Integer.parseInt(splits[1]);
                this.removesCeiling = Boolean.parseBoolean(splits[2]);
                this.secret = Boolean.parseBoolean(splits[3]);
            } catch (Exception ex) {
                WitcheryCompanion.logger.warn("Could not deserialize CapacityBrewActionInfo from string: {}", str, ex);
            }

        }

        @Override
        public boolean isSecret() {
            return this.secret;
        }

        @Override
        public String getSecretKey() {
            return ProgressUtils.getBrewActionSecret(this.stack);
        }
    }
}
