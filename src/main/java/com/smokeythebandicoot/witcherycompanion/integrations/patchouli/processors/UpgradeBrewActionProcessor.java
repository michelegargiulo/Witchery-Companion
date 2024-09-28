package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import net.msrandom.witchery.brewing.action.UpgradeBrewAction;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;
import java.util.function.Function;


public class UpgradeBrewActionProcessor extends BrewActionProcessor {

    protected boolean upgradesPower;
    protected String finalDescription;
    protected String description;
    protected int increment;
    protected int ceiling;
    protected Function<Integer, String> incrementTransformFunction;

    private static final Map<Integer, BrewActionInfo> powerCache = new HashMap<>();
    private static final Map<Integer, BrewActionInfo> durationCache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");

        String upgradesPowerStr = readVariable(provider, "upgrade_type");
        this.upgradesPower = upgradesPowerStr == null || !upgradesPowerStr.equals("duration");

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
        if (upgradesPower && powerCache.containsKey(index) || !upgradesPower && durationCache.containsKey(index)) {
            if (upgradesPower) info = powerCache.get(index);
            else info = durationCache.get(index);
        }
        else {

            UpgradeBrewAction action = upgradesPower ?
                    BrewRegistry.getPower(index) :
                    BrewRegistry.getDuration(index);

            if (action != null) {
                this.isSecret = action.getHidden();
                this.stack = action.getKey().toStack();

                this.increment = action.getIncrease();
                this.ceiling = action.getLimit();

                this.finalDescription = getDescription();

                obfuscateIfSecret();

                info = new BrewActionInfo(
                        ItemStackUtil.serializeStack(this.stack),
                        this.finalDescription);

                if (upgradesPower) powerCache.put(index, info);
                else durationCache.put(index, info);
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
        String transformed = this.incrementTransformFunction.apply(this.ceiling);
        if (this.description == null) return transformed;
        return this.description.replace("{limit}", transformed) + (this.isSecret ? this.secretText : "");
    }

    public static void clearCache() {
        powerCache.clear();
        durationCache.clear();
    }

}
