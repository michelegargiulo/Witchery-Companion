package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.HashMap;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class SecretTextProcessor extends BaseProcessor {

    private String progressKey;
    private String title;
    private String description;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> provider) {
        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.progressKey = readVariable(provider, "secret_key");
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "text":
                return this.description;
        }
        return null;
    }

    @Override
    protected String getSecretKey() {
        // Since this is generic, the secret key must be fully-qualified (but without namespace)
        // For example "creatures/hobgoblins/trading"
        return this.progressKey;
    }

    @Override
    protected void obfuscateFields() {
        this.title = obfuscate(this.title, EObfuscationMethod.PATCHOULI);
        this.description = obfuscate(this.description, EObfuscationMethod.MINECRAFT);
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
    }


}
