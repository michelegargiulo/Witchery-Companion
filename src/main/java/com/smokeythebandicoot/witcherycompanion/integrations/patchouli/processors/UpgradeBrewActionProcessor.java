package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import net.minecraft.item.ItemStack;
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
    protected Function<Integer, String> incrementTransformFunction;

    private static final Map<Integer, UpgradeBrewActionInfo> powerCache = new HashMap<>();
    private static final Map<Integer, UpgradeBrewActionInfo> durationCache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");
        this.secretTooltip = readVariable(provider, "secret_tooltip");

        String upgradesPowerStr = readVariable(provider, "upgrade_type");
        this.upgradesPower = upgradesPowerStr == null || !upgradesPowerStr.equals("duration");

        // Read capacity-specific template variables
        this.description = readVariable(provider, "description");

        String transformFunc = readVariable(provider, "transform_function");
        switch (transformFunc) {
            case "double":
                this.incrementTransformFunction = val -> String.valueOf(val * 2);
            case "roman":
                this.incrementTransformFunction = RomanNumbers::toRoman;
            default:
                this.incrementTransformFunction = String::valueOf;
        }

        // Book contents are reloaded, invalidate caches
        powerCache.clear();
        durationCache.clear();
    }

    @Override
    public String process(String key) {

        int index = ProcessorUtils.splitKeyIndex(key);

        UpgradeBrewActionInfo info = null;
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

                this.finalDescription = getDescription();

                obfuscateIfSecret();

                info = new UpgradeBrewActionInfo(
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
        this.stack = OBFUSCATED_STACK;
        obfuscate(this.finalDescription, EObfuscationMethod.PATCHOULI);
        super.obfuscateFields();
    }

    @Override
    protected void hideFields() {
        this.stack = ItemStack.EMPTY;
        this.finalDescription = "";
        super.hideFields();
    }

    public String getDescription() {
        String transformed = this.incrementTransformFunction.apply(this.increment);
        if (this.description == null) return transformed;
        return this.description.replace("{increment}", transformed);
    }


    private static class UpgradeBrewActionInfo {

        private final String serializedStack;
        private final String description;

        private UpgradeBrewActionInfo(String serializedStack, String description) {
            this.serializedStack = serializedStack;
            this.description = description;
        }
    }
}
