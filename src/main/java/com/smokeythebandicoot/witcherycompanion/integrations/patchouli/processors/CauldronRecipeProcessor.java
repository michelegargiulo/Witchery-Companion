package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.CauldronRecipe;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;
import java.util.stream.Collectors;


/** This processor is responsible for generating a list of items for a CauldronRecipeComponent from a Cauldron Recipe **/
public class CauldronRecipeProcessor implements IComponentProcessor {

    private String recipeOutput = null;

    private String recipeOutput2 = null;

    private List<Ingredient> inputs = null;

    private List<Ingredient> inputs2 = null;

    private boolean isDouble = false;

    private static Map<String, CauldronRecipe> cauldronRecipeMap = null;


    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {

        // Read first output
        if (iVariableProvider.has("output_item")) {
            this.recipeOutput = iVariableProvider.get("output_item");
        }
        // Read second output
        if (iVariableProvider.has("output_item2")) {
            this.recipeOutput2 = iVariableProvider.get("output_item2");
            this.isDouble = true;
        }

        // Double recipe can be forced
        if (iVariableProvider.has("double")) {
            this.isDouble = Boolean.parseBoolean(iVariableProvider.get("double"));
        }

        // Init data structures for first time
        if (cauldronRecipeMap == null || cauldronRecipeMap.isEmpty()) {
            updateCauldronRecipes();
        }

        // Retrieve stacks depending on recipe
        // For first item
        inputs = new ArrayList<>();
        if (this.recipeOutput != null) {
            String canonicStack = ProcessorUtils.getCanonic(this.recipeOutput);
            if (cauldronRecipeMap.containsKey(canonicStack)){
            inputs.addAll(cauldronRecipeMap.get(canonicStack).getInputs()
                    .stream()
                    .map(CauldronRecipe.PoweredItem::getIngredient)
                    .collect(Collectors.toList()));
            }
            inputs.add(cauldronRecipeMap.get(canonicStack).getTriggerIngredient().getIngredient());
        }

        // And second item, if it is double
        if (!this.isDouble)
            return;
        inputs2 = new ArrayList<>();
        if (this.recipeOutput2 != null) {
            String canonicStack2 = ProcessorUtils.getCanonic(this.recipeOutput2);
            if (cauldronRecipeMap.containsKey(canonicStack2)){
                inputs2.addAll(cauldronRecipeMap.get(canonicStack2).getInputs()
                        .stream()
                        .map(CauldronRecipe.PoweredItem::getIngredient)
                        .collect(Collectors.toList()));
            }
            inputs2.add(cauldronRecipeMap.get(canonicStack2).getTriggerIngredient().getIngredient());
        }
    }

    @Override
    public String process(String key) {

        // Inputs of first recipe
        if (key.equals("inputs_first")) {
            return ProcessorUtils.serializeIngredientList(inputs);

        // Output of first recipe
        } else if (key.equals("output_first")) {
            return this.recipeOutput;

        // Inputs for second recipe
        } else if (key.equals("inputs_second") && isDouble && inputs2 != null && recipeOutput2 != null) {
            return ProcessorUtils.serializeIngredientList(inputs2);

        // Output of second recipe
        } else if (key.equals("output_second") && isDouble && inputs2 != null && recipeOutput2 != null) {
            return this.recipeOutput2;

        // Flag for whether to draw second recipe
        } else if (key.equals("processor_isdouble")) {
            return String.valueOf(this.isDouble);

        }

        return null;
    }

    @Override
    public boolean allowRender(String group) {
        return group.equals("second_recipe") && this.isDouble && inputs2 != null && recipeOutput2 != null;
    }

    private static void updateCauldronRecipes() {
        cauldronRecipeMap = new HashMap<>();
        List<CauldronRecipe> recipes = WitcheryUtils.getRecipeManager(null).getRecipesForType(WitcheryRecipeTypes.CAULDRON);
        for (CauldronRecipe recipe : recipes) {
            String output_item = ItemStackUtil.serializeStack(recipe.getRecipeOutput());
            cauldronRecipeMap.put(output_item, recipe);
        }
    }


}
