package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ImpShinyWrapper extends BaseRecipeWrapper {

    protected List<ItemStack> fixedGifts;
    protected List<ItemStack> randomGifts;
    private static List<ItemStack> lootGenCache = null;

    public ImpShinyWrapper(IGuiHelper guiHelper) {
        this(guiHelper, "witchery.imp_shiny");
    }

    public ImpShinyWrapper(IGuiHelper guiHelper, String uidIn) {
        this.uid = uidIn;


        /*inputs.add(recipe.getItemToBuy().copy());
        if (recipe.hasSecondItemToBuy()) {
            inputs.add(recipe.getSecondItemToBuy().copy());
        }

        outputs.add(recipe.getItemToSell().copy());*/
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>());
        //ingredients.setOutputs(ItemStack.class, outputs);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }


}
