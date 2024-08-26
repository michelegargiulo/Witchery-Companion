package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;


/** This processor is responsible for passing the #brew variable from template to the CauldronBrewComponent **/
public class CauldronBrewProcessor implements IComponentProcessor {

    private String brewId = null;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {
        if (iVariableProvider.has("cauldron_brew")) {
            this.brewId = iVariableProvider.get("cauldron_brew");
        }
    }

    @Override
    public String process(String key) {
        if (this.brewId != null && key.equals("cauldron_brew")) {
           return brewId;
        }
        return null;
    }

}
