package com.smokeythebandicoot.witcherycompanion.integrations.jei.mutandis;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class MutandisClayWrapper extends BaseRecipeWrapper {

    protected final ItemStack input;
    protected final ItemStack output;
    protected final boolean isClayConversion;

    public MutandisClayWrapper(ItemStack input, ItemStack output, boolean isClayConversion) {
        this.input = input;
        this.output = output;
        this.isClayConversion = isClayConversion;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputs = Collections.singletonList(input);
        if (isClayConversion) inputs.add(new ItemStack(Items.WATER_BUCKET));
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(input));
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(output));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
