package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brazier.IBrazierSummoningRecipeAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
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

    private static Map<String, BrazierRecipe> brazierRecipeMap;
    private List<Ingredient> stacks;
    private BrazierRecipeInfo recipeInfo;
    private String forcedTitle;
    private String forcedDesc;
    private boolean shouldShow = true;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {

        if (iVariableProvider.has("recipe_id")) {
            this.recipeId = iVariableProvider.get("recipe_id");
        }

        if (iVariableProvider.has("title")) {
            this.forcedTitle = iVariableProvider.get("title");
        }

        if (iVariableProvider.has("description")) {
            this.forcedDesc = iVariableProvider.get("description");
        }

        // Init data structures for first time
        if (brazierRecipeMap == null || brazierRecipeMap.isEmpty()) {
            updateBrazierMap();
        }

        if (!brazierRecipeMap.containsKey(recipeId))
            return;

        BrazierRecipe recipe = brazierRecipeMap.get(recipeId);

        stacks = new ArrayList<>();
        stacks.addAll(recipe.getIngredients());

        this.recipeInfo = new BrazierRecipeInfo(recipe);
        this.shouldShow = shouldShow(recipeInfo);

    }

    @Override
    public String process(String key) {
        switch (key) {
            case "guard":
                return String.valueOf(this.shouldShow);
            case "inputs":
                return stacks == null ? null : ProcessorUtils.serializeIngredientList(stacks);
            case "title":
                if (forcedTitle != null) return forcedTitle;
                return recipeInfo == null ? null : recipeInfo.title;
            case "text":
                if (forcedDesc != null) return forcedDesc;
                return recipeInfo == null ? null : recipeInfo.description;
            case "secret_guard":
                return (recipeInfo != null && recipeInfo.secret && this.shouldShow) ? "true" : "";
            case "extra":
                return recipeInfo.extra;
            case "extra_guard":
                return this.shouldShow && !recipeInfo.extra.isEmpty() ? "t" : "";
        }
        return null;
    }

    private static void updateBrazierMap() {
        brazierRecipeMap = new HashMap<>();
        for (BrazierRecipe recipe : WitcheryUtils.getRecipeManager(null).getRecipesForType(WitcheryRecipeTypes.BRAZIER)) {
            brazierRecipeMap.put(recipe.getId().toString(), recipe);
        }
    }

    private static boolean shouldShow(BrazierRecipeInfo info) {
        // Recipe is not secret, always show
        if (!info.secret) return true;

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS && hasUnlockedProgress(info);
    }

    private static boolean hasUnlockedProgress(BrazierRecipeInfo info) {
        // Get secret key and return true if the corresponding element has been found
        String key = ProgressUtils.getBrazierRecipeSecret(info.id);
        return ClientProxy.getLocalWitcheryProgress().hasProgress(key);
    }


    public static class BrazierRecipeInfo {

        public final String id;
        public final String title;
        public final String description;
        public final String extra;
        public final boolean secret;

        public BrazierRecipeInfo(@Nonnull BrazierRecipe recipe) {
            String[] splits = recipe.getDescription(0.0f).split("\n\n");

            // Try retrieve title
            if (splits.length > 0) {
                this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
            } else {
                this.title = "<no title>";
            }

            // Try retrieve description
            if (splits.length > 1) {
                this.description = ProcessorUtils.reformatPatchouli(splits[1], false);
            } else {
                this.description = "<no description>";
            }

            this.id = recipe.getId().toString();
            this.secret = recipe.getHidden();

            String extraInfo = "";
            if (recipe instanceof IBrazierSummoningRecipeAccessor) {
                IBrazierSummoningRecipeAccessor summonRecipe = (IBrazierSummoningRecipeAccessor) recipe;
                Class<EntityCreature> creatureClass = summonRecipe.getExtraSpawnedEntity().getEntityClass();

                ResourceLocation entityRegName = EntityList.getKey(creatureClass);
                if (entityRegName != null) {
                    extraInfo = I18n.format("entity." + EntityList.getTranslationName(entityRegName) + ".name");
                }
            }
            this.extra = extraInfo;
        }


    }

}
