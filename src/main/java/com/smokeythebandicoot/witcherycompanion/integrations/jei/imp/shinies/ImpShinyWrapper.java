package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class ImpShinyWrapper extends BaseRecipeWrapper {

    public HashMap<ItemStack, Integer> shiniesPage;

    public ImpShinyWrapper(IGuiHelper guiHelper) {
        shiniesPage = new HashMap<>();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // It is an input-only category, showing what Imps can accept
        ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>(this.shiniesPage.keySet()));
        ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<>());
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
