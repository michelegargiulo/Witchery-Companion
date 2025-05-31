package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.generic;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;


public class SecretSpotlightProcessor extends BaseProcessor {

    protected String progressKey;
    protected String title;
    protected String description;
    protected String item;

    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> provider) {
        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.item = readVariable(provider, "item");
        this.secretText = readVariable(provider, "secret_text");

        if (this.title == null) {
            ItemStack stack = ItemStackUtil.loadStackFromString(this.item);
            this.title = stack.getDisplayName();
        }

        this.progressKey = readVariable(provider, "secret_key");
        this.isSecret = true;
        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "title":
                return this.title;
            case "description":
                return this.description;
            case "item":
                return this.item;
            case "secret_text":
                return this.secretText;
            default:
                super.process(key);
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
        this.item = ItemStackUtil.serializeStack(OBFUSCATED_STACK);
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
        this.item = "";
    }


}
