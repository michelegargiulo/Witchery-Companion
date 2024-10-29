package com.smokeythebandicoot.witcherycompanion.integrations.jei.mutandis;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;


public class MutandisPlantWrapper extends BaseRecipeWrapper {

    protected List<List<ItemStack>> itemsAtPage;

    public MutandisPlantWrapper(IGuiHelper guiHelper) {
        this.itemsAtPage = new ArrayList<>();
    }

    public void addItem(IBlockState state) {
        ItemStack stack = Utils.blockstateToStack(state);
        itemsAtPage.add(Collections.singletonList(stack));
    }

    public void addItem(Ingredient ingredient) {
        itemsAtPage.add(Arrays.asList(ingredient.getMatchingStacks()));
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutputLists(VanillaTypes.ITEM, itemsAtPage);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
