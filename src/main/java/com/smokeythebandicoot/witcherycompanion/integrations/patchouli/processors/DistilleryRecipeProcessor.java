package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.DistilleryRecipeDTO;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.DistilleryRecipe;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.HashMap;
import java.util.Map;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class DistilleryRecipeProcessor implements IComponentProcessor {

    private String recipeId = null;
    private DistilleryRecipeDTO dto = null;
    private static final Map<String, String> dtoCache = new HashMap<>();

    @Override
    public void setup(IVariableProvider<String> provider) {
        if (provider.has("distillery_recipe")) {
            this.recipeId = provider.get("distillery_recipe");
            DistilleryRecipe recipe = ContentUtils.getRecipeForType(WitcheryRecipeTypes.DISTILLERY, this.recipeId);
            if (recipe != null)
                dto = new DistilleryRecipeDTO(recipe);
            dtoCache.put(dto.secretKey, ProcessorUtils.serializeDto(dto));
        }
    }

    @Override
    public String process(String key) {
        if (key.equals("serialized") && dtoCache.containsKey(dto.secretKey)) {
            return dtoCache.get(dto.secretKey);
        }
        return null;
    }

}
