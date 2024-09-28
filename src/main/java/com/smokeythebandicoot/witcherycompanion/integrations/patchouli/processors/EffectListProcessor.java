package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import vazkii.patchouli.api.IVariableProvider;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class EffectListProcessor extends BaseProcessor {

    protected String effectLevelList;
    protected String effectFormat = "{level}";

    @Override
    public void setup(IVariableProvider<String> provider) {
        this.isSecret = false; // Makes no sense to have secret effect list
        this.effectFormat = readVariable(provider, "effect_format");
        this.effectLevelList = computeList();
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        if (key.equals("effect_list")) {
            return this.effectLevelList;
        }
        return null;
    }

    @Override
    public String getSecretKey() {
        return "";
    }

    @Override
    protected void obfuscateFields() { }

    @Override
    protected void hideFields() { }

    protected String computeList() {
        StringBuilder sb = new StringBuilder();
        for (int level : BrewRegistry.getEffects().keySet()) {
            sb.append(effectFormat.replace("{level}", String.valueOf(level)));
        }
        return sb.toString();
    }

}
