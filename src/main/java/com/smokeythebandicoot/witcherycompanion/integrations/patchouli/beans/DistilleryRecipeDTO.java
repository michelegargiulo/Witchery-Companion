package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.recipe.DistilleryRecipe;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;

public class DistilleryRecipeDTO extends AbstractDTO {

    // All set by recipe
    @Expose public Ingredient firstIng = Ingredient.EMPTY;
    @Expose public Ingredient secondIng = Ingredient.EMPTY;
    @Expose public List<ItemStack> outputs = new ArrayList<>();
    @Expose public int clayJars = 0;
    public String secretKey = null;

    @Override
    protected void initFields() {
        mapField("distillery_recipe", null,
                () -> ProcessorUtils.serializeDto(this));

        mapField("first_ingredient",
                str -> this.firstIng = ItemStackUtil.loadIngredientFromString(str),
                () -> this.getHideState() == EHiddenState.CLEARTEXT ? ItemStackUtil.serializeIngredient(this.firstIng) : null);

        mapField("second_ingredient",
                str -> this.secondIng = ItemStackUtil.loadIngredientFromString(str),
                () -> this.getHideState() == EHiddenState.CLEARTEXT ? ItemStackUtil.serializeIngredient(this.secondIng) : null);

        mapField("outputs",
                str -> this.outputs = ProcessorUtils.deserializeItemStackList(str),
                () -> ProcessorUtils.serializeItemStackList(this.getHideState() == EHiddenState.CLEARTEXT ? this.outputs : new ArrayList<>()));

        mapField("clay_jars",
                str -> this.clayJars = Utils.tryParseInt(str),
                () -> String.valueOf(this.clayJars));
    }


    public DistilleryRecipeDTO() { }

    public DistilleryRecipeDTO(DistilleryRecipe recipe) {
        this.firstIng = recipe.getPrimaryIngredient();
        this.secondIng = recipe.getSecondaryIngredient();
        this.isSecret = false;
        this.outputs = recipe.getOutputs();
        this.clayJars = recipe.getJars();
        this.secretKey = recipe.getId().toString();
    }

    @Override
    public String getSecretKey() {
        if (this.secretKey == null) return null;
        return "distilling/recipes/" + this.secretKey;
    }
}
