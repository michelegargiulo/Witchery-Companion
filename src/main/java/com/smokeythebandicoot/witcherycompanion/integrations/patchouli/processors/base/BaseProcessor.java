package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Collection;


public abstract class BaseProcessor implements IComponentProcessor {

    protected boolean isSecret = false;
    protected String secretText = "";
    protected String secretTooltip = "";

    protected static ItemStack OBFUSCATED_STACK = new ItemStack(Items.PAPER);
    protected static Ingredient OBFUSCATED_INGREDIENT = Ingredient.fromStacks(new ItemStack(Items.PAPER));

    /** Super should be called at the end */
    @Override
    public void setup(IVariableProvider<String> provider) {
        secretText = readVariable(provider, "secret_text");
        secretTooltip = readVariable(provider, "secret_tooltip");

        obfuscateIfSecret();
    }

    /** Super should be called at the end */
    @Override
    public String process(String key) {
        switch (key) {
            // A flag that is true when the content is secret
            case "is_secret":
                return String.valueOf(this.isSecret);

            // A string that represents the "Secret" text
            case "secret_text":
                return this.secretText;
            case "secret_tooltip":
                return this.secretTooltip;
        }
        return null;
    }

    /** Reads a variable from a provider */
    public String readVariable(IVariableProvider<String> provider, String key) {
        return provider.has(key) ? provider.get(key) : null;
    }

    protected abstract String getSecretKey();

    protected EHiddenState getHideState() {

        // If not secret, it should be shown regardless of everything
        if (!this.isSecret || this.getSecretKey() == null)
            return EHiddenState.UNLOCKED;

        // Otherwise, Check if secrets should always be shown
        PatchouliIntegration.EPatchouliSecretPolicy policy = PatchouliIntegration.common_showSecretsPolicy;

        // Always show: should not hide, always show text
        if (policy == PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return EHiddenState.UNLOCKED;

            // Never show: should be hidden
        else if (policy == PatchouliIntegration.EPatchouliSecretPolicy.DISABLED)
            return EHiddenState.DISABLED;

        // Progress: show only if player has progress
        return ClientProxy.getLocalWitcheryProgress().hasProgress(this.getSecretKey()) ? EHiddenState.UNLOCKED : EHiddenState.LOCKED;
    }

    protected void obfuscateIfSecret() {

        switch (this.getHideState()) {
            case UNLOCKED:
                // Everything is in clear text
                return;
            case LOCKED:
                // Obfuscate self fields and let child classes obfuscate theirs
                // Secret text and Tooltip are edge cases, as the "Secret" text should
                // not appear if the page is unlocked (while other components should be
                // obfuscated, not hidden)
                this.secretText = "";
                this.secretTooltip = "";
                obfuscateFields();
                break;
            case DISABLED:
                // Hide self fields and let child classes hide theirs
                this.secretText = "";
                this.secretTooltip = "";
                hideFields();
        }
    }

    protected abstract void obfuscateFields();

    protected abstract void hideFields();

    /** Depending on how the text should be hidden, return proper string.
     * Do not use on non-text objects! (like stacks or ingredients, just titles and descriptions) */
    protected String obfuscate(String text, String obfStart, String obfEnd) {
        return obfStart + ProcessorUtils.reformatPatchouli(text, true) + obfEnd;
    }

    /** Depending on how the text should be hidden, return proper string.
     * Do not use on non-text objects! (like stacks or ingredients, just titles and descriptions) */
    protected String obfuscate(String text, EObfuscationMethod obfuscationMethod) {
        return obfuscate(text, obfuscationMethod.obfStart, obfuscationMethod.obfEnd);
    }

    protected ItemStack obfuscate(ItemStack stack) {
        return OBFUSCATED_STACK;
    }

    protected Ingredient obfuscate(Ingredient ingredient) {
        return OBFUSCATED_INGREDIENT;
    }

    protected void obfuscateStackList(Collection<ItemStack> stacks) {
        int size = stacks.size();
        stacks.clear();
        for (int i = 0; i < size; i++) {
            stacks.add(OBFUSCATED_STACK);
        }
    }

    protected void obfuscateIngredientList(Collection<Ingredient> ingredients) {
        int size = ingredients.size();
        ingredients.clear();
        for (int i = 0; i < size; i++) {
            ingredients.add(OBFUSCATED_INGREDIENT);
        }
    }

    protected enum EHiddenState {
        UNLOCKED,
        LOCKED,
        DISABLED
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
