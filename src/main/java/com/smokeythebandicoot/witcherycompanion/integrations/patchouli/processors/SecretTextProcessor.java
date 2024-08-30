package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.HashMap;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class SecretTextProcessor implements IComponentProcessor {

    private String progressKey;
    private String title;
    private String description;
    private boolean shouldShow = true;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {

        if (iVariableProvider.has("secret_key")) {
            this.progressKey = iVariableProvider.get("secret_key");
            // If not namespaced, then namespace it
            if (!this.progressKey.startsWith(WitcheryCompanion.MODID)) {
                this.progressKey = WitcheryCompanion.prefix(this.progressKey);
            }
        }

        if (iVariableProvider.has("title")) {
            this.title = iVariableProvider.get("title");
        }

        if (iVariableProvider.has("description")) {
            this.description = iVariableProvider.get("description");
        }

        this.shouldShow = shouldShow(this.progressKey);

    }

    @Override
    public String process(String key) {
        switch (key) {
            case "guard":
                return this.shouldShow ? "true" : "";
            case "title":
                return this.title;
            case "text":
                return this.description;
        }
        return null;
    }

    private static boolean shouldShow(String progressKey) {

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS &&
                ClientProxy.getLocalWitcheryProgress().hasProgress(progressKey);
    }

}
