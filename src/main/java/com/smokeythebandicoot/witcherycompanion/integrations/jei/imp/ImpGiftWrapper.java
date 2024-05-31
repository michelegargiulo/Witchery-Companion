package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible
 */
public class ImpGiftWrapper extends BaseRecipeWrapper {

    protected List<ItemStack> fixedGifts;
    protected List<ItemStack> randomGifts;

    public ImpGiftWrapper(IGuiHelper guiHelper) {
        this(guiHelper, "witchery.imp_gift");
    }

    public ImpGiftWrapper(IGuiHelper guiHelper, String uidIn) {
        this.uid = uidIn;
        fixedGifts = new ArrayList<>();
        randomGifts = new ArrayList<>();

        /*inputs.add(recipe.getItemToBuy().copy());
        if (recipe.hasSecondItemToBuy()) {
            inputs.add(recipe.getSecondItemToBuy().copy());
        }

        outputs.add(recipe.getItemToSell().copy());*/
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(ItemStack.class, new ArrayList<>());
        //ingredients.setOutputs(ItemStack.class, outputs);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
