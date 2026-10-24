package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.TinkersTools;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.ToolArthana;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.ToolBoline;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.tools.Pickaxe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Hegemony: Tinkers Construct items that are the equivalent of a Witchery: Resurrected item can be used in place of it.
 * Every hook that wants to know "is this stack a Witchery X?" asks {@link #isEquivalent}, {@link #resolveAs} (for hooks
 * that compare Items) or {@link #getEquivalents} (for ingredients, that can accept several items).
 * Equivalents can be registered by anyone with {@link #register} and are only considered if the
 * option "common_tweakAllowHegemony" is true. Broken Tinkers items must never be registered as equivalent.
 **/
public class Hegemony {

    private static final Map<Item, List<Predicate<ItemStack>>> EQUIVALENTS = new LinkedHashMap<>();

    public static boolean isEnabled() {
        return TinkersIntegration.enableTinkersIntegration && TinkersIntegration.common_tweakAllowHegemony;
    }

    /** Registers a predicate that tells if a stack is the Tinkers equivalent of the given Witchery item **/
    public static void register(Item witcheryItem, Predicate<ItemStack> equivalent) {
        if (witcheryItem == null || equivalent == null) return;
        EQUIVALENTS.computeIfAbsent(witcheryItem, k -> new ArrayList<>()).add(equivalent);
    }

    /** Returns true if Hegemony is enabled and the stack is the Tinkers equivalent of the given Witchery item **/
    public static boolean isEquivalent(ItemStack stack, Item witcheryItem) {
        if (stack == null || stack.isEmpty() || witcheryItem == null || !isEnabled()) return false;
        List<Predicate<ItemStack>> predicates = EQUIVALENTS.get(witcheryItem);
        if (predicates == null) return false;
        for (Predicate<ItemStack> predicate : predicates) {
            if (predicate.test(stack)) return true;
        }
        return false;
    }

    /** For hooks that compare a stack's Item with a specific Witchery item: returns {@code wanted} if the stack is
     * the Tinkers equivalent of it, otherwise {@code actual} (the stack's own Item, as returned by the original call) **/
    public static Item resolveAs(ItemStack stack, Item actual, Item wanted) {
        return isEquivalent(stack, wanted) ? wanted : actual;
    }

    /** Returns every Witchery item that this stack is the Tinkers equivalent of (a Silver-headed Arthana is both an
     * Arthana and a Silver Sword). Empty if Hegemony is disabled or the stack has no equivalents **/
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

    /** Registers the equivalents provided by W:C **/
    public static void registerDefaults() {
        register(WitcheryGeneralItems.getArthana(), stack ->
                stack.getItem() instanceof ToolArthana && !ToolHelper.isBroken(stack));

        register(WitcheryGeneralItems.BOLINE, stack ->
                stack.getItem() instanceof ToolBoline && !ToolHelper.isBroken(stack));

        // Any Tinkers tool with a Silver head, as a sword is a wooden stick with two silver ingots
        register(WitcheryGeneralItems.SILVER_SWORD, stack ->
                isHeadMadeOf(stack, "silver") && !ToolHelper.isBroken(stack));

        // Pickaxes with the Goblin's Favor trait (the Koboldite material grants it)
        register(WitcheryGeneralItems.KOBOLDITE_PICKAXE, stack ->
                stack.getItem() instanceof Pickaxe &&
                        TinkersUtils.hasEffect(stack, Integration.TRAIT_GOBLINS_FAVOR));
    }

    /** Returns true if the stack is a Tinkers tool whose head is made of the material with the given identifier **/
    public static boolean isHeadMadeOf(ItemStack stack, String materialIdentifier) {
        if (!(stack.getItem() instanceof TinkersItem) || stack.getTagCompound() == null) return false;
        List<PartMaterialType> parts = ((TinkersItem) stack.getItem()).getRequiredComponents();
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        for (int i = 0; i < parts.size() && i < materials.size(); i++) {
            if (parts.get(i).usesStat(MaterialTypes.HEAD)) {
                return materialIdentifier.equals(materials.get(i).identifier);
            }
        }
        return false;
    }
}
