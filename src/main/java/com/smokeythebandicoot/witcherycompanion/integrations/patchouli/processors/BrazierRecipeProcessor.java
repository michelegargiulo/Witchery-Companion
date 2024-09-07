package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brazier.IBrazierSummoningRecipeAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.BrazierRecipeDTO;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.DistilleryRecipeDTO;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.DistilleryRecipe;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class BrazierRecipeProcessor implements IComponentProcessor {

    private String recipeId = null;
    private BrazierRecipeDTO dto = null;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> provider) {

        if (provider.has("brazier_recipe")) {
            this.recipeId = provider.get("brazier_recipe");
            BrazierRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.BRAZIER, this.recipeId);
            if (recipe != null)
                dto = new BrazierRecipeDTO(recipe);
        }

    }

    @Override
    public String process(String key) {
        if (dto != null) return dto.getForKey(key);
        return null;
    }

}
