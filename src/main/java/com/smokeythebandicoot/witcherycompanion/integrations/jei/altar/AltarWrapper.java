package com.smokeythebandicoot.witcherycompanion.integrations.jei.altar;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AltarWrapper extends BaseRecipeWrapper {

    public List<List<ItemStack>> itemsAtPage;

    public AltarWrapper(IGuiHelper guiHelper) {
        itemsAtPage = new ArrayList<>();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // It is an input-only category, showing what blocks can boost Altar
        ingredients.setInputLists(VanillaTypes.ITEM, itemsAtPage);
        ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<>());
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
