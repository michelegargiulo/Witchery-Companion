package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.SpiritEffectApi;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import vazkii.patchouli.api.IVariableProvider;


/** This processor is responsible for processing templates that represent SpiritEffectRecipes **/
public class SpiritEffectRecipeProcessor extends BaseProcessor {

    protected String title;
    protected String description;
    protected String beings;
    protected String secretKey;


    @Override
    public void setup(IVariableProvider<String> provider) {

        // Effect_id must be namespaced
        String effectId = readVariable(provider, "effect_id");
        if (effectId == null) return;

        SpiritEffectRecipe recipe = SpiritEffectApi.getById(new ResourceLocation(effectId));
        if (recipe == null) return;

        this.isSecret = recipe.getHidden();
        this.secretKey = effectId;

        // Override title and description if not provided by the symbol effect
        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.beings = readVariable(provider, "required_beings");

        String[] splits = recipe.getDescription().split("\n\n");
        if (splits.length > 3) {
            if (this.title == null) this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
            if (this.description == null) this.description = splits[1];
            if (this.beings == null) this.beings = ProcessorUtils.reformatPatchouli(splits[2] + "$(br)" + splits[3], false);
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
            case "beings":
                return this.beings;
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return ProgressUtils.getSpiritEffectRecipeSecret(this.secretKey);
    }

    @Override
    protected void obfuscateFields() {
        this.title = obfuscate(this.title, EObfuscationMethod.MINECRAFT);
        this.description = obfuscate(this.description, EObfuscationMethod.PATCHOULI);
        this.beings = obfuscate(this.beings, EObfuscationMethod.PATCHOULI);
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
        this.beings = "";
    }

}
