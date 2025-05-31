package com.smokeythebandicoot.witcherycompanion.integrations.jei.suncollector;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;


public class SunCollectorWrapper extends BaseRecipeWrapper {

    protected final ItemStack input;
    protected final ItemStack output;

    public SunCollectorWrapper(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(input));
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(output));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
