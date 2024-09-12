package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.recipe.DistilleryRecipe;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class DistilleryRecipeProcessor extends BaseProcessor {

    protected String recipeId = null;
    protected String title = "";
    protected Ingredient firstIng = Ingredient.EMPTY;
    protected Ingredient secondIng = Ingredient.EMPTY;
    protected List<ItemStack> outputs = new ArrayList<>();
    protected int clayJars = 0;

    @Override
    public void setup(IVariableProvider<String> provider) {

        this.recipeId = readVariable(provider, "distillery_recipe");
        if (this.recipeId == null) return;

        DistilleryRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.DISTILLERY, recipeId);
        if (recipe == null) return;

        this.isSecret = false; // Distillery does not have secret recipes
        this.firstIng = recipe.getPrimaryIngredient();
        this.secondIng = recipe.getSecondaryIngredient();
        this.outputs = recipe.getOutputs();
        this.clayJars = recipe.getJars();

        if (!outputs.isEmpty()) {
            this.title = outputs.get(0).getDisplayName();
        }

        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "input0":
                return ItemStackUtil.serializeIngredient(this.firstIng);
            case "input1":
                return ItemStackUtil.serializeIngredient(this.secondIng);
            case "clay_jars":
                ItemStack clayJarStack;
                if (this.clayJars < 0) return null;
                clayJarStack = new ItemStack(WitcheryFumeItems.CLAY_JAR, this.clayJars);
                return ItemStackUtil.serializeStack(clayJarStack);
            case "output0":
                if (this.outputs != null && !this.outputs.isEmpty())
                    return ItemStackUtil.serializeStack(this.outputs.get(0));
                return null;
            case "output1":
                if (this.outputs != null && this.outputs.size() > 1)
                    return ItemStackUtil.serializeStack(this.outputs.get(1));
                return null;
            case "output2":
                if (this.outputs != null && this.outputs.size() > 2)
                    return ItemStackUtil.serializeStack(this.outputs.get(2));
                return null;
            case "output3":
                if (this.outputs != null && this.outputs.size() > 3)
                    return ItemStackUtil.serializeStack(this.outputs.get(3));
                return null;
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return ProgressUtils.getDistilleryRecipeSecret(this.recipeId);
    }

    @Override
    protected void obfuscateFields() {
        this.firstIng = OBFUSCATED_INGREDIENT;
        this.secondIng = OBFUSCATED_INGREDIENT;
        this.clayJars = -1;
        obfuscateStackList(this.outputs);
    }

    @Override
    protected void hideFields() {
        this.firstIng = Ingredient.EMPTY;
        this.secondIng = Ingredient.EMPTY;
        this.clayJars = -1;
        this.outputs = new ArrayList<>();
    }

}
