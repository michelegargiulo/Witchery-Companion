package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.generic;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import vazkii.patchouli.api.IVariableProvider;


public class SecretTextProcessor extends BaseProcessor {

    private String progressKey;
    private String title;
    private String description;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> provider) {
        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.isSecret = true;
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
        this.title = obfuscate(this.title, EObfuscationMethod.MINECRAFT);
        this.description = obfuscate(this.description, EObfuscationMethod.PATCHOULI);
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
    }


}
