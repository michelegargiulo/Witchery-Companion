package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.CauldronRecipe;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;


public class CauldronRecipeProcessor extends BaseProcessor {

    protected String title;
    protected String description;

    protected String firstRecipeId;
    protected List<Ingredient> firstRecipeInputs = new ArrayList<>();
    protected ItemStack firstRecipeOutput = ItemStack.EMPTY;
    protected String firstRecipePower;

    protected String secondRecipeId;
    protected List<Ingredient> secondRecipeInputs = new ArrayList<>();
    protected ItemStack secondRecipeOutput = ItemStack.EMPTY;
    protected String secondRecipePower;

    protected transient boolean switchRecipe = false;
    protected transient String currentRecipeSecretKey;
    protected transient boolean isDouble = false;


    @Override
    public void setup(IVariableProvider<String> provider) {

        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.isDouble = false;

        this.firstRecipeId = readVariable(provider, "first_recipe_id");
        this.secondRecipeId = readVariable(provider, "second_recipe_id");

        // If first recipe is null but second is not, then second becomes first
        if (secondRecipeId != null && firstRecipeId == null) {
            this.firstRecipeId = secondRecipeId;
            this.secondRecipeId = null;
        }

        firstRecipeBlock: {
            // If first recipe is still null, return null
            if (firstRecipeId == null) break firstRecipeBlock;

            CauldronRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.CAULDRON, this.firstRecipeId);
            if (recipe == null) break firstRecipeBlock;

            this.firstRecipeInputs = recipe.getIngredients();
            this.firstRecipeOutput = recipe.getRecipeOutput();
            this.firstRecipePower = String.valueOf(recipe.getPower());

            // We set currentRecipeSecretKey to only obfuscate keys of first recipe
            // We read secret_text and secret_tooltip twice, but we reuse a lot of code this way
            switchRecipe = true;
            this.currentRecipeSecretKey = ProgressUtils.getCauldronRecipeSecret(recipe.getId().getPath());
            super.setup(provider);
        }

        secondRecipeBlock: {
            // If second recipe is still null, return null
            if (secondRecipeId == null) break secondRecipeBlock;

            // If not, set isDouble to true
            this.isDouble = true;

            CauldronRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.CAULDRON, this.secondRecipeId);
            if (recipe == null) break secondRecipeBlock;

            this.secondRecipeInputs = recipe.getIngredients();
            this.secondRecipeOutput = recipe.getRecipeOutput();
            this.secondRecipePower = String.valueOf(recipe.getPower());

            this.currentRecipeSecretKey = ProgressUtils.getCauldronRecipeSecret(recipe.getId().getPath());
            super.setup(provider);
        }

    }

    @Override
    public String process(String key) {

        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "first_recipe_inputs":
                return ProcessorUtils.serializeIngredientList(this.firstRecipeInputs);
            case "first_recipe_output":
                return ItemStackUtil.serializeStack(this.firstRecipeOutput);
            case "first_recipe_power":
                return this.firstRecipePower;
            case "second_recipe_inputs":
                return ProcessorUtils.serializeIngredientList(this.secondRecipeInputs);
            case "second_recipe_output":
                return ItemStackUtil.serializeStack(this.secondRecipeOutput);
            case "second_recipe_power":
                return this.secondRecipePower;
            default:
                return super.process(key);
        }
    }

    // Since the page has two separate recipes, we override the obfuscateIfSecret directly
    @Override
    protected String getSecretKey() {
        return this.currentRecipeSecretKey;
    }

    @Override
    protected void obfuscateFields() {
        if (switchRecipe) {
            obfuscateIngredientList(this.secondRecipeInputs);
            this.secondRecipeOutput = OBFUSCATED_STACK;
            this.secondRecipePower = obfuscate(this.secondRecipePower, EObfuscationMethod.PATCHOULI);
        } else {
            obfuscateIngredientList(this.firstRecipeInputs);
            this.firstRecipeOutput = OBFUSCATED_STACK;
            this.firstRecipePower = obfuscate(this.firstRecipePower, EObfuscationMethod.PATCHOULI);
        }
    }

    @Override
    protected void hideFields() {
        if (switchRecipe) {
            this.secondRecipeInputs = new ArrayList<>();
            this.secondRecipeOutput = ItemStack.EMPTY;
            this.secondRecipePower = "";
        } else {
            this.firstRecipeInputs = new ArrayList<>();
            this.firstRecipeOutput = ItemStack.EMPTY;
            this.firstRecipePower = "";
        }
    }

    @Override
    public boolean allowRender(String group) {
        return group.equals("second_recipe") && this.isDouble;
    }


}
