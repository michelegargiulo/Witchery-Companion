package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import net.msrandom.witchery.brewing.action.IncrementBrewAction;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class EffectBrewActionProcessor extends BrewActionProcessor {

    protected boolean upgradesExtent;
    protected String finalDescription;
    protected String description;
    protected int increment;
    protected int ceiling;
    protected Function<Integer, String> incrementTransformFunction;

    private static final Map<Integer, BrewActionInfo> extentCache = new HashMap<>();
    private static final Map<Integer, BrewActionInfo> lingeringCache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");

        String upgradesPowerStr = readVariable(provider, "upgrade_type");
        this.upgradesExtent = upgradesPowerStr == null || !upgradesPowerStr.equals("lingering");

        // Read capacity-specific template variables
        this.description = readVariable(provider, "description");

        String transformFunc = readVariable(provider, "transform_function");
        switch (transformFunc) {
            case "double":
                this.incrementTransformFunction = val -> String.valueOf(val * 2);
                break;
            case "roman":
                this.incrementTransformFunction = RomanNumbers::toRoman;
                break;
            default:
                this.incrementTransformFunction = String::valueOf;
                break;
        }
    }

    @Override
    public String process(String key) {

        int index = ProcessorUtils.splitKeyIndex(key);

        BrewActionInfo info = null;
        if (upgradesExtent && extentCache.containsKey(index) || !upgradesExtent && lingeringCache.containsKey(index)) {
            if (upgradesExtent) info = extentCache.get(index);
            else info = lingeringCache.get(index);
        }
        else {

            IncrementBrewAction action = upgradesExtent ?
                    BrewRegistry.getExtent(index) :
                    BrewRegistry.getLingering(index);

            if (action != null) {
                this.isSecret = action.getHidden();
                this.stack = action.getKey().toStack();

                this.increment = 1; // Increment is always 1
                this.ceiling = action.getLimit();

                this.finalDescription = getDescription();

                obfuscateIfSecret();

                info = new BrewActionInfo(
                        ItemStackUtil.serializeStack(this.stack),
                        this.finalDescription);

                if (upgradesExtent) extentCache.put(index, info);
                else lingeringCache.put(index, info);
            }
        }

        if (info == null) return null;

        if (key.startsWith("stack")) {
            return info.serializedStack;
        } else if (key.startsWith("description")) {
            return info.description;
        }

        // This does not call super, and is_enabled always returns true, as it is the BrewRegistry itself
        // that adds or removes components
        return null;
    }

    @Override
    protected void obfuscateFields() {
        obfuscate(this.finalDescription, EObfuscationMethod.PATCHOULI);
        super.obfuscateFields();
    }

    @Override
    protected void hideFields() {
        this.finalDescription = "";
        super.hideFields();
    }

    public String getDescription() {
        String transformed = this.incrementTransformFunction.apply(this.ceiling + 1);
        if (this.description == null) return transformed;
        return this.description.replace("{limit}", transformed) + (this.isSecret ? this.secretText : "");
    }

    public static void clearCache() {
        extentCache.clear();
        lingeringCache.clear();
    }

}
