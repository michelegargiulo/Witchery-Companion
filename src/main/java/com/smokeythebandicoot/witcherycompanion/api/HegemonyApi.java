package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Hegemony: items of other mods (such as Tinkers Construct's) that are the equivalent of a Witchery: Resurrected item
 * can be used in place of it, everywhere Witchery checks for it (rites, loot, altar, ...).
 * <p>
 * This class has no dependencies on any other mod, so it is safe to use (and to hook with Mixins) when those mods are
 * not installed: without registered equivalents it does nothing. Integrations register their equivalents with
 * {@link #register}. Hooks ask {@link #isEquivalent}, {@link #resolveAs} (for hooks that compare Items) or
 * {@link #getEquivalents} (for ingredients, that can accept several items). Everything is disabled if the option
 * "common_tweakAllowHegemony" is false. Broken items should never be registered as equivalent
 **/
public class HegemonyApi {

    private static final Map<Item, List<Predicate<ItemStack>>> EQUIVALENTS = new LinkedHashMap<>();

    public static boolean isEnabled() {
        return TinkersIntegration.enableTinkersIntegration && TinkersIntegration.common_tweakAllowHegemony;
    }

    /** Registers a predicate that tells if a stack is the equivalent of the given Witchery item **/
    public static void register(Item witcheryItem, Predicate<ItemStack> equivalent) {
        if (witcheryItem == null || equivalent == null) return;
        EQUIVALENTS.computeIfAbsent(witcheryItem, k -> new ArrayList<>()).add(equivalent);
    }

    /** Returns true if Hegemony is enabled and the stack is the equivalent of the given Witchery item **/
    public static boolean isEquivalent(ItemStack stack, Item witcheryItem) {
        if (stack == null || stack.isEmpty() || witcheryItem == null || EQUIVALENTS.isEmpty() || !isEnabled()) return false;
        List<Predicate<ItemStack>> predicates = EQUIVALENTS.get(witcheryItem);
        if (predicates == null) return false;
        for (Predicate<ItemStack> predicate : predicates) {
            if (predicate.test(stack)) return true;
        }
        return false;
    }

    /** For hooks that compare a stack's Item with a specific Witchery item: returns {@code wanted} if the stack is
     * the equivalent of it, otherwise {@code actual} (the stack's own Item, as returned by the original call) **/
    public static Item resolveAs(ItemStack stack, Item actual, Item wanted) {
        return isEquivalent(stack, wanted) ? wanted : actual;
    }

    /** Returns every Witchery item that this stack is the equivalent of (a Silver-headed Arthana is both an Arthana
     * and a Silver Sword). Empty if Hegemony is disabled or the stack has no equivalents **/
    public static List<Item> getEquivalents(ItemStack stack) {
        if (stack == null || stack.isEmpty() || EQUIVALENTS.isEmpty() || !isEnabled()) return Collections.emptyList();
        List<Item> result = null;
        for (Item witcheryItem : EQUIVALENTS.keySet()) {
            if (isEquivalent(stack, witcheryItem)) {
                if (result == null) result = new ArrayList<>();
                result.add(witcheryItem);
            }
        }
        return result == null ? Collections.<Item>emptyList() : result;
    }
}
