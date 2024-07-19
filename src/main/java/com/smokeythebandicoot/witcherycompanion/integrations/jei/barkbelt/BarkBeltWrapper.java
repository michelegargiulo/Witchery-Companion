package com.smokeythebandicoot.witcherycompanion.integrations.jei.barkbelt;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BarkBeltWrapper extends BaseRecipeWrapper {

    protected List<List<ItemStack>> itemsAtPage;

    public BarkBeltWrapper(IGuiHelper guiHelper) {
        this.itemsAtPage = new ArrayList<>();
    }

    public void addItemsForSlot(List<ItemStack> items) {
        itemsAtPage.add(items);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<>());

        if (itemsAtPage == null) {
            ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>());
            return;
        }

        // Flatten inputs
        ingredients.setInputs(VanillaTypes.ITEM, itemsAtPage.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
