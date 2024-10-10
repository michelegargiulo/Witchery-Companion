package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.item.crafting;

import com.google.common.base.Predicate;
import com.smokeythebandicoot.witcherycompanion.api.recipes.IIngredientAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements Predicate<ItemStack>, IIngredientAccessor {

    @Shadow(remap = true) @Final
    private ItemStack[] matchingStacks;

    @Override
    public ItemStack[] getAllMatchingStacks() {
        return this.matchingStacks;
    }
}
