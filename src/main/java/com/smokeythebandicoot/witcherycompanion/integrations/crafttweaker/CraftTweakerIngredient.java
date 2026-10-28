package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.CustomRiteApi;
import com.smokeythebandicoot.witcherycompanion.api.customrite.NbtMatch;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ingredient of an item of a custom rite. Matching is delegated to the CraftTweaker ingredient (so anyDamage(),
 * withTag(tag, true), only(function), ore dictionary entries... behave as they do in CraftTweaker recipes), optionally
 * combined with a "required tag": the item must contain at least those NBT entries (partial match, lists too), or
 * have exactly that NBT.
 * W:R tests one thrown item at a time and consumes one unit per requirement, so the item is tested as a single unit.
 */
public class CraftTweakerIngredient extends Ingredient {

    private final IIngredient ingredient;
    @Nullable
    private final NBTTagCompound requiredTag;
    private final boolean exactTag;
    private boolean errorLogged = false;

    private CraftTweakerIngredient(IIngredient ingredient, @Nullable NBTTagCompound requiredTag, boolean exactTag, ItemStack[] display) {
        super(display);
        this.ingredient = ingredient;
        this.requiredTag = requiredTag;
        this.exactTag = exactTag;
    }

    /** Null if the ingredient has no concrete item to display in the books (for example a wildcard <*>) */
    @Nullable
    public static CraftTweakerIngredient create(IIngredient ingredient, @Nullable NBTTagCompound requiredTag, boolean exactTag) {
        List<ItemStack> display = new ArrayList<>();
        List<IItemStack> items = ingredient.getItems();
        if (items != null) {
            for (IItemStack item : items) {
                ItemStack stack = CraftTweakerMC.getItemStack(item);
                if (stack.isEmpty()) continue;
                stack = stack.copy();
                stack.setCount(1);
                // Show the required NBT in the books and previews, unless the item already carries NBT
                if (requiredTag != null && !requiredTag.isEmpty() && !stack.hasTagCompound()) {
                    stack.setTagCompound(requiredTag.copy());
                }
                display.add(stack);
            }
        }
        if (display.isEmpty()) return null;
        return new CraftTweakerIngredient(ingredient, requiredTag, exactTag, display.toArray(new ItemStack[0]));
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        try {
            ItemStack single = stack.copy();
            single.setCount(1);
            boolean result = (requiredTag == null || matchesTag(single))
                    && ingredient.matches(CraftTweakerMC.getIItemStack(single));
            if (CustomRiteApi.isDebugEnabled()) {
                WitcheryCompanion.logger.info("[Custom Rites debug] item test: {} tag={} -> {}", single, single.getTagCompound(), result);
            }
            return result;
        } catch (Throwable t) {
            // A script condition failed. Do not crash the circle: the item simply does not match
            if (!errorLogged) {
                errorLogged = true;
                WitcheryCompanion.logger.error("A condition of a custom rite ingredient threw an exception. Items do not match", t);
            }
            return false;
        }
    }

    private boolean matchesTag(ItemStack stack) {
        return exactTag ? NbtMatch.exactly(requiredTag, stack.getTagCompound()) : NbtMatch.contains(requiredTag, stack.getTagCompound());
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
