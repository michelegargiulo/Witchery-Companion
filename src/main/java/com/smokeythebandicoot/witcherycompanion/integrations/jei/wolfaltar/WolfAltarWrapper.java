package com.smokeythebandicoot.witcherycompanion.integrations.jei.wolfaltar;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;


public class WolfAltarWrapper extends BaseRecipeWrapper {

    protected final ItemStack input;
    protected final ItemStack output;
    protected final int minLevel;

    public WolfAltarWrapper(ItemStack input, ItemStack output, int minLevel) {
        this.input = input;
        this.output = output;
        this.minLevel = minLevel;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(input));
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(output));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(I18n.format("witcherycompanion.gui.wolf_altar.min_level") + minLevel, 2, 36, 0x000000);
    }

}
