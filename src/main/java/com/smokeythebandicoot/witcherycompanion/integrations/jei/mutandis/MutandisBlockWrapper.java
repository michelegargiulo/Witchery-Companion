package com.smokeythebandicoot.witcherycompanion.integrations.jei.mutandis;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@ParametersAreNonnullByDefault
public class MutandisBlockWrapper extends BaseRecipeWrapper {

    protected final ItemStack input;
    protected final ItemStack output;
    protected final boolean isClayConversion;

    public MutandisBlockWrapper(ItemStack input, ItemStack output, boolean isClayConversion) {
        this.input = input;
        this.output = output;
        this.isClayConversion = isClayConversion;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputs = new ArrayList<>(Collections.singletonList(input));
        if (isClayConversion) inputs.add(new ItemStack(Items.WATER_BUCKET));
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(input));
        ingredients.setOutputs(VanillaTypes.ITEM, Collections.singletonList(output));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

}
