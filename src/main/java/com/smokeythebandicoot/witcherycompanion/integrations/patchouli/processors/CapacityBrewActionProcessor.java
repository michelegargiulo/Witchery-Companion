package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.api.brewing.ICapacityBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
import java.util.Map;


public class CapacityBrewActionProcessor extends BrewActionProcessor {

    protected int increment = 0;
    protected int ceiling = 0;
    protected boolean removesCeiling = false;
    protected String removesCeilingText;
    protected String removesCeilingTooltip;

    private static final Map<Integer, CapacityBrewActionInfo> cache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");
        this.secretTooltip = readVariable(provider, "secret_tooltip");

        // Read capacity-specific template variables
        this.removesCeilingText = readVariable(provider, "removes_ceiling_text");
        this.removesCeilingTooltip = readVariable(provider, "removes_ceiling_tooltip");

        // Other variables are read in the process()

        // Book contents are reloaded, invalidate caches
        cache.clear();
    }

    @Override
    public String process(String key) {

        int index = ProcessorUtils.splitKeyIndex(key);

        // Caching avoids re-computing of all fields when a single brew has multiple fields
        // In this case we have stack and description
        CapacityBrewActionInfo info = null;
        if (cache.containsKey(index)) {
            info = cache.get(index);
        }
        else {

            CapacityBrewAction capacityBrewAction = BrewRegistry.getCapacity(index);
            if (capacityBrewAction != null) {

                // Variables from superclass
                this.isSecret = capacityBrewAction.getHidden();
                this.stack = capacityBrewAction.getKey().toStack();

                // Increment and ceiling
                this.increment = capacityBrewAction.getIncrement();
                this.ceiling = capacityBrewAction.getCeiling();

                // Get removes ceiling using Mixins instead of reflection
                if ((Object) capacityBrewAction instanceof ICapacityBrewActionAccessor) {
                    ICapacityBrewActionAccessor accessor = (ICapacityBrewActionAccessor) (Object) capacityBrewAction;
                    this.removesCeiling = accessor.getRemoveCeiling();
                }

                // Description (at the end because other fields need to be set)
                this.description = getDescription();

                // We ignore brewName and brewType, as we do not use them in this template

                // Call obfuscate fields
                obfuscateIfSecret();

                // Now that we have all the info, we cache it into a CapacityBrewActionInfo
                info = new CapacityBrewActionInfo(ItemStackUtil.serializeStack(this.stack), this.description);
                cache.put(index, info);
            }
        }

        if (info == null) {
            return null;
        }

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
        this.removesCeiling = false;
        obfuscate(this.removesCeilingText, EObfuscationMethod.MINECRAFT);
        obfuscate(this.removesCeilingTooltip, EObfuscationMethod.PATCHOULI);
        super.obfuscateFields();
    }

    @Override
    protected void hideFields() {
        this.removesCeiling = false;
        this.removesCeilingText = "";
        this.removesCeilingTooltip = "";
        super.hideFields();
    }

    public String getDescription() {
        // Draw the "+X" string (always present)
        StringBuilder sb = new StringBuilder("+");
        sb.append(this.increment);

        // Decorate with more info, if any
        if (this.isSecret || this.removesCeiling) {
            sb.append(" (");
            if (this.isSecret)
                sb.append("$(t:").append(this.secretTooltip).append(")").append(this.secretText).append("$(/t)");
            if (this.removesCeiling) {
                if (this.isSecret) sb.append(", ");
                sb.append("$(t:").append(this.removesCeilingTooltip).append(")").append(this.removesCeilingText).append("$(/t)");
            }
            sb.append(")");
        }

        return sb.toString();
    }


    private static class CapacityBrewActionInfo {

        private String serializedStack;
        private String description;

        private CapacityBrewActionInfo(String serializedStack, String description) {
            this.serializedStack = serializedStack;
            this.description = description;
        }
    }

}
