package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import vazkii.patchouli.api.IVariableProvider;


/** This processor is responsible for passing the #brew variable from template to the CauldronBrewComponent **/
public class CauldronBrewProcessor extends BrewActionProcessor {

    private String brewType = "";

    @Override
    public void setup(IVariableProvider<String> provider) {
        String brewId = readVariable(provider, "cauldron_brew");
        brewType = readVariable(provider, "brew_type");
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "brew_type":
                return this.brewType;
            case "brew_name":
                return this.brewName;
            case "text":
                return this.description;
            case "inputs":
                //return ProcessorUtils.serializeIngredientList(this.)
            case "output":
            default:
                return super.process(key);
        }
    }

    @Override
    protected void obfuscateFields() {
        obfuscate(this.brewName, EObfuscationMethod.PATCHOULI);
        obfuscate(this.brewType, EObfuscationMethod.PATCHOULI);
        obfuscate(this.description, EObfuscationMethod.MINECRAFT);
    }

    @Override
    protected void hideFields() {
        this.brewName = "";
        this.brewType = "";
    }

}
