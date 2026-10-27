package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ingredient of an item of a custom rite. Matching is delegated to the CraftTweaker ingredient (so anyDamage(),
 * withTag(tag, true), only(function), ore dictionary entries... behave as they do in CraftTweaker recipes), optionally
 * combined with a "required tag": the item must contain at least those NBT entries (partial match, lists too).
 * W:R tests one thrown item at a time and consumes one unit per requirement, so the item is tested as a single unit.
 */
public class CraftTweakerIngredient extends Ingredient {

    private final IIngredient ingredient;
    @Nullable
    private final NBTTagCompound requiredTag;
    private boolean errorLogged = false;

    private CraftTweakerIngredient(IIngredient ingredient, @Nullable NBTTagCompound requiredTag, ItemStack[] display) {
        super(display);
        this.ingredient = ingredient;
        this.requiredTag = requiredTag;
    }

    /** Null if the ingredient has no concrete item to display in the books (for example a wildcard <*>) */
    @Nullable
    public static CraftTweakerIngredient create(IIngredient ingredient, @Nullable NBTTagCompound requiredTag) {
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
        return new CraftTweakerIngredient(ingredient, requiredTag, display.toArray(new ItemStack[0]));
    }

    /** True if the stack contains every entry of the required tag. An empty required tag matches everything */
    public static boolean containsTag(NBTTagCompound required, ItemStack stack) {
        if (required.isEmpty()) return true;
        return NBTUtil.areNBTEquals(required, stack.getTagCompound(), true);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        try {
            ItemStack single = stack.copy();
            single.setCount(1);
            if (requiredTag != null && !containsTag(requiredTag, single)) return false;
            return ingredient.matches(CraftTweakerMC.getIItemStack(single));
        } catch (Throwable t) {
            // A script condition failed. Do not crash the circle: the item simply does not match
            if (!errorLogged) {
                errorLogged = true;
                WitcheryCompanion.logger.error("A condition of a custom rite ingredient threw an exception. Items do not match", t);
            }
            return false;
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }

}
