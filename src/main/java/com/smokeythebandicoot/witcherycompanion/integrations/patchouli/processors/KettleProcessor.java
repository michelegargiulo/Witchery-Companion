package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.KettleRecipe;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;


public class KettleProcessor extends BaseProcessor {

    protected String recipeId;
    protected String title;
    protected String description;
    protected String note;
    protected List<Ingredient> ingredients;
    protected ItemStack output;
    protected float altarPower = 0;


    @Override
    public void setup(IVariableProvider<String> provider) {
        ingredients = new ArrayList<>();
        output = ItemStack.EMPTY;

        this.recipeId = readVariable(provider, "brew_id");
        if (recipeId == null) return;

        KettleRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.KETTLE, recipeId);
        if (recipe == null) return;

        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.note = readVariable(provider, "note");

        this.ingredients = recipe.getInputs();
        this.output = recipe.getRecipeOutput();
        this.altarPower = recipe.getPowerRequired();

        if (this.title == null && !this.output.isEmpty()) {
            this.title = this.output.getDisplayName();
        }

        this.isSecret = false;
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "note":
                return this.note;
            case "ingredients":
                return ProcessorUtils.serializeIngredientList(this.ingredients);
            case "output":
                return ItemStackUtil.serializeStack(this.output);
            case "altar_power":
                return String.valueOf(this.altarPower);
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return ProgressUtils.getKettleSecret(this.recipeId);
    }

    @Override
    protected void obfuscateFields() { }

    @Override
    protected void hideFields() { }



}
