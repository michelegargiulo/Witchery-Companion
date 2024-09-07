package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;


public abstract class AbstractDTO implements ISecretInfo {

    // Common keys for all DTOs
    @Expose public boolean isSecret = false;
    @Expose public String secretText = "";
    @Expose public String secretTooltip = "";

    private Map<String, Supplier<String>> serializationMap;
    private Map<String, Consumer<String>> deserializationMap;

    {
        initCommonFields();
    }

    public AbstractDTO() { }

    @Override
    public boolean isSecret() {
        return this.isSecret;
    }

    protected EHiddenState getHideState() {

        // If not secret, it means it is written in the manual, so show it
        if (!this.isSecret() || this.getSecretKey() == null)
            return EHiddenState.CLEARTEXT;

        // Otherwise, Check if secrets should always be shown
        PatchouliIntegration.EPatchouliSecretPolicy policy = PatchouliIntegration.common_showSecretsPolicy;

        // Always show: should not hide, always show text
        if (policy == PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return EHiddenState.CLEARTEXT;

        // Never show: should be hidden
        else if (policy == PatchouliIntegration.EPatchouliSecretPolicy.DISABLED)
            return EHiddenState.INVISIBLE;

        // Progress: show only if player has progress
        return ClientProxy.getLocalWitcheryProgress().hasProgress(this.getSecretKey()) ? EHiddenState.CLEARTEXT : EHiddenState.OBFUSCATED;
    }

    /** Depending on how the text should be hidden, return proper string.
     * Do not use on non-text objects! (like stacks or ingredients, just titles and descriptions) */
    protected String obfuscateIfSecret(String text, String obfStart, String obfEnd) {
        switch (this.getHideState()) {
            case INVISIBLE:
                return null;
            case OBFUSCATED:
                return obfStart + ProcessorUtils.reformatPatchouli(text, true) + obfEnd;
            default:
                return text;
        }
    }

    /** Depending on how the text should be hidden, return proper string.
     * Do not use on non-text objects! (like stacks or ingredients, just titles and descriptions) */
    protected String obfuscateIfSecret(String text, EObfuscationMethod obfuscationMethod) {
        return obfuscateIfSecret(text, obfuscationMethod.obfStart, obfuscationMethod.obfEnd);
    }

    /** This functions initializes the mappers and registers the mapping for the secret fields (common for all DTOs) */
    private void initCommonFields() {
        deserializationMap = new HashMap<>();
        serializationMap = new HashMap<>();

        // Map Secret fields
        mapField("is_secret",
                str -> this.isSecret = Boolean.parseBoolean(str),
                () -> String.valueOf(this.isSecret));

        mapField("secret_text",
                str -> this.secretText = str,
                () -> obfuscateIfSecret(this.secretText, EObfuscationMethod.PATCHOULI));

        mapField("secret_tooltip",
                str -> this.secretTooltip = str,
                () -> obfuscateIfSecret(this.secretTooltip, EObfuscationMethod.MINECRAFT));

        initFields();
    }

    protected abstract void initFields();

    /** Registers a new field into the mapping */
    protected final void mapField(String fieldName, Consumer<String> stringToField, Supplier<String> fieldToString) {
        serializationMap.put(fieldName, fieldToString);
        deserializationMap.put(fieldName, stringToField);
    }

    /** This function uses the mapper to bind a field in the class to a string that has to be passed to a ICustomComponent */
    public String getForKey(String key) {
        if (serializationMap.containsKey(key) && serializationMap.get(key) != null) {
            return serializationMap.get(key).get();
        }
        return null;
    }

    protected enum EHiddenState {
        CLEARTEXT,
        OBFUSCATED,
        INVISIBLE
    }

    protected enum EObfuscationMethod {

        // Indicated for text that appears in Tooltips, as Patchouli tooltip formatting requires
        // Parenthesis, and Patchouli obfuscation format requires them too, breaking formatting
        MINECRAFT("§k", "§r"),

        // Indicated for text that appears in Page Text, as for some reason Patchouli does not always recognize
        // obfuscation text in-between tooltip formatting $(t:TOOLTIP)TEXT$(/t)
        PATCHOULI("$(k)", "$()");

        EObfuscationMethod(String obfStart, String obfEnd) {
            this.obfStart = obfStart;
            this.obfEnd = obfEnd;
        }

        public final String obfStart;
        public final String obfEnd;

    }
}
