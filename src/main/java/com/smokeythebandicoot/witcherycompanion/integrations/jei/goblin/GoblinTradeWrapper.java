package com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class GoblinTradeWrapper extends BaseRecipeWrapper {

    protected List<ItemStack> inputs;
    protected List<ItemStack> outputs;

    public GoblinTradeWrapper(IGuiHelper guiHelper, MerchantRecipe recipe) {
        this(guiHelper, recipe, "witchery.goblin_trade");
    }

    public GoblinTradeWrapper(IGuiHelper guiHelper, MerchantRecipe recipe, String uidIn) {
        this.uid = uidIn;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        inputs.add(recipe.getItemToBuy().copy());
        if (recipe.hasSecondItemToBuy()) {
            inputs.add(recipe.getSecondItemToBuy().copy());
        }

        outputs.add(recipe.getItemToSell().copy());
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
