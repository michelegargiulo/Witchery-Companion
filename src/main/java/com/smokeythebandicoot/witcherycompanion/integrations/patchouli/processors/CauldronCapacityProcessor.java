package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.brewing.ICapacityBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.utils.CapabilityUtils;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;


public class CauldronCapacityProcessor implements IComponentProcessor {

    private static List<CapacityBrewActionInfo> capacityBrews = null;

    private static final String REMOVE_CEILING_TOOLTIP =
            "Brews can have indefinitely many effects, but they can be augmented - in power or duration - a limited " +
                    "amount of times. Using this item disables this ceiling, allowing for very powerful brews";

    private static final String SECRET_TOOLTIP =
            "This item is secret. Does not show in this book for a Player which didn't use it in a brew before";


    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        if (capacityBrews == null) {
            updateCapacityMap();
        }
    }

    @Override
    public String process(String key) {
        // This processor does not touch this key
        if (!key.startsWith("item")) return null;

        // Update map in case it is null
        if (capacityBrews == null || capacityBrews.isEmpty()) {
            updateCapacityMap();
        }

        // Convert the set into a sorted list accessible by index
        int index = ProcessorUtils.getIndexFromKey(key, "item");

        if (index > -1 && index < capacityBrews.size()) {
            CapacityBrewActionInfo info = capacityBrews.get(index);

            // Checks if it is secret and if player has knowledge about it
            ProgressSync.clientRequest();
            if (!shouldShow(info))
                return null;

            // Set itemstack and description
            if (key.endsWith("_cap")) {
                String suffix = "";
                // Decorates the description with "(Removes Ceiling, Secret)", including tooltips for their meaning
                if (info.removesCeiling)
                    suffix = " ($(t:" + REMOVE_CEILING_TOOLTIP + ")Removes Ceiling$(/t)" +
                            (info.secret ? ", $(t:" + SECRET_TOOLTIP + "Secret$(/t))" : ")");
                else if (info.secret)
                    suffix = " $(t:" + SECRET_TOOLTIP + ")(Secret)$(/t)";
                return "+" + info.increment + suffix;
            } else {
                return ItemStackUtil.serializeStack(info.stack);
            }
        }
        WitcheryCompanion.logger.warn("Could not parse key for CauldronCapacityProcessor. Key outside of bounds. " +
                "Key: {}, Capacity Brew Size: {}", key, capacityBrews == null ? "null" : capacityBrews.size());
        return null;
    }

    /** ========== HELPER METHODS ========== **/
    private static void updateCapacityMap() {
        SortedSet<CapacityBrewActionInfo> sortedBrews = new TreeSet<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof CapacityBrewAction) {
                CapacityBrewAction capacityBrewAction = (CapacityBrewAction) action;
                int increment = capacityBrewAction.getIncrement();
                boolean removeCeiling = false;
                boolean secret = action.getHidden();
                // Should be capacityBrewAction instead of action, but compiler now knows that capacityBrewAction
                // is an instance of CapacityBrewAction, which does not implement ICapacityBrewActionAccessor (as
                // it is injected at startup by mixins)
                if (action instanceof ICapacityBrewActionAccessor) {
                    ICapacityBrewActionAccessor accessor = (ICapacityBrewActionAccessor) action;
                    removeCeiling = accessor.getRemoveCeiling();
                }
                sortedBrews.add(new CapacityBrewActionInfo(
                        capacityBrewAction.getKey().toStack(),
                        increment,
                        removeCeiling,
                        secret));
            }
        }
        capacityBrews = new ArrayList<>(sortedBrews);
    }

    private static boolean shouldShow(CapacityBrewActionInfo info) {
        // If not secret, it means it is written in the manual, so show it
        if (!info.secret)
            return true;

        // Otherwise, Check if secrets should always be shown
        PatchouliIntegration.EPatchouliSecretPolicy policy = PatchouliIntegration.common_showSecretsPolicy;
        if (policy == PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS && hasUnlockedProgress(info);
    }

    private static boolean hasUnlockedProgress(CapacityBrewActionInfo info) {
        // Get secret key and return true if the corresponding element has been found
        String key = CapabilityUtils.getBrewingCapacitySecret(info.stack);
        return ClientProxy.getLocalWitcheryProgress().hasProgress(key);
    }

    /** ========== EXPOSED METHODS ========== **/

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

    /** ========== INFO HOLDER CLASS ========== **/
    public static class CapacityBrewActionInfo implements Comparable<CapacityBrewActionInfo> {

        public final ItemStack stack;
        public final int increment;
        public final boolean removesCeiling;
        public final boolean secret;

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
    }
}
