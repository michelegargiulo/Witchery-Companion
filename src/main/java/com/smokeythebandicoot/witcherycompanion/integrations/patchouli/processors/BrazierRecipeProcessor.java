package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.accessors.brazier.IBrazierSummoningRecipeAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class BrazierRecipeProcessor extends BaseProcessor {

    public String title;
    public String description;
    public List<Ingredient> inputs;
    public boolean hasExtraEntity;
    public String extraEntity;

    public String secretKey;


    @Override
    public void setup(IVariableProvider<String> provider) {

        String recipeId = readVariable(provider, "brazier_recipe");
        if (recipeId == null) return;

        BrazierRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.BRAZIER, recipeId);
        if (recipe == null) return;

        String[] splits = recipe.getDescription(0.0f).split("\n\n");

        // Title and description
        if (splits.length > 0) this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
        if (splits.length > 1) this.description = ProcessorUtils.reformatPatchouli(splits[1], false);

        // Recipe
        this.inputs = recipe.getIngredients();
        this.isSecret = recipe.getHidden();
        this.hasExtraEntity = this.extraEntity != null;
        this.secretKey = recipe.getId().toString();

        // Extra entity
        if (recipe instanceof IBrazierSummoningRecipeAccessor) {
            IBrazierSummoningRecipeAccessor summonRecipe = (IBrazierSummoningRecipeAccessor) recipe;
            Class<EntityCreature> creatureClass = summonRecipe.getExtraSpawnedEntity().getEntityClass();

            ResourceLocation entityRegName = EntityList.getKey(creatureClass);
            if (entityRegName != null) {
                this.extraEntity = I18n.format("entity." + EntityList.getTranslationName(entityRegName) + ".name");
            }
        }

        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "inputs":
                return ProcessorUtils.serializeIngredientList(this.inputs);
            case "has_extra_entity":
                return String.valueOf(this.hasExtraEntity);
            case "extra_entity":
                return this.extraEntity;
            default:
                return super.process(key);
        }
    }

    @Override
    public String getSecretKey() {
        return ProgressUtils.getBrazierRecipeSecret(this.secretKey);
    }

    @Override
    protected void obfuscateFields() {
        this.title = obfuscate(this.title, EObfuscationMethod.MINECRAFT);
        this.description = obfuscate(this.description, EObfuscationMethod.PATCHOULI);
        obfuscateIngredientList(this.inputs);
        this.hasExtraEntity = false;
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
        this.inputs = new ArrayList<>();
        this.hasExtraEntity = false;
    }

}
