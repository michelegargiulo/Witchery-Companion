package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritEffect;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import net.msrandom.witchery.resources.SpiritEffectManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;


/** This processor is responsible for processing templates that represent SpiritEffectRecipes **/
public class SpiritEffectRecipeProcessor extends BaseProcessor {

    protected String title;
    protected String description;
    protected String beings;

    private static Map<String, SpiritEffectRecipe> spiritEffectMap = new HashMap<>();


    @Override
    public void setup(IVariableProvider<String> provider) {

        updateSpiritEffectMap();

        String effectId = readVariable(provider, "effect_id");
        if (effectId == null) return;

        SpiritEffectRecipe recipe = SpiritEffectManager.INSTANCE




        if (iVariableProvider.has("effect_id")) {
            this.recipeId = iVariableProvider.get("effect_id");
        }

        if (iVariableProvider.has("title")) {
            this.forcedTitle = iVariableProvider.get("title");
        }

        if (iVariableProvider.has("description")) {
            this.forcedDesc = iVariableProvider.get("description");
        }

        if (iVariableProvider.has("required_beings")) {
            this.forcedBeings = iVariableProvider.get("required_beings");
        }

        // Init data structures for first time
        if (recipeMap == null || recipeMap.isEmpty()) {
            updateRecipeMap();
        }

        if (!recipeMap.containsKey(recipeId))
            return;

        this.recipeInfo = recipeMap.get(recipeId);

        if (!shouldShow(recipeInfo)) { // It's updated on Book Reload (so also when progress is unlocked)
            this.shouldShow = false;
        }

    }

    @Override
    public String process(String key) {
        switch (key) {
            case "guard":
                return String.valueOf(this.shouldShow);
            case "title":
                if (forcedTitle != null) return forcedTitle;
                return recipeInfo == null ? null : recipeInfo.title;
            case "text":
                if (forcedDesc != null) return forcedDesc;
                return recipeInfo == null ? null : recipeInfo.description;
            case "beings":
                if (forcedBeings != null) return forcedBeings;
                return recipeInfo == null ? null : recipeInfo.beings;
            case "secret_guard":
                if (recipeInfo == null) return null;
                return this.shouldShow && recipeInfo.secret ? "true" : "";
        }
        return null;
    }

    private static void updateSpiritEffectMap() {
        spiritEffectMap.clear();
        for (SpiritEffectRecipe recipe : SpiritEffectManager.INSTANCE.getEffects()) {

            ResourceLocation id = InfusedSpiritEffect.REGISTRY.getKey(recipe.getResult());
            if (id == null) continue;

            String key = "fetish." + id.getNamespace() + '.' + id.getPath();
            spiritEffectMap.put(key, recipe);
        }
    }

    private static boolean shouldShow(SpiritEffectRecipeInfo info) {
        // Recipe is not secret, always show
        if (!info.secret) return true;

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS && hasUnlockedProgress(info);
    }

    private static boolean hasUnlockedProgress(SpiritEffectRecipeInfo info) {
        // Get secret key and return true if the corresponding element has been found
        String key = ProgressUtils.getSpiritEffectRecipeSecret(info.id);
        return ClientProxy.getLocalWitcheryProgress().hasProgress(key);
    }



    public static class SpiritEffectRecipeInfo {

        public final String id;
        public final String title;
        public final String description;
        public final String beings;
        public final boolean secret;

        public SpiritEffectRecipeInfo(@Nonnull SpiritEffectRecipe recipe) {
            String[] splits = recipe.getDescription().split("\n\n");

            // Retrieve title
            if (splits.length > 0) {
                this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
            } else {
                this.title = "<unnamed effect>";
            }

            // Retrieve description
            if (splits.length > 1) {
                this.description = ProcessorUtils.reformatPatchouli(splits[1], false);
            } else {
                this.description = "<unknown details>";
            }

            if (splits.length > 3) {
                this.beings = ProcessorUtils.reformatPatchouli(splits[3], false);
            } else {
                this.beings = "<unknown required beings>";
            }

            this.id = title.replace(" ", "_").toLowerCase();
            this.secret = recipe.getHidden();

        }


    }

}
