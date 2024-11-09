package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.api.accessors.brewing.ICapacityBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
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

    private static final Map<Integer, BrewActionInfo> cache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        // We do not call super, so we read manually secret text and tooltip
        this.secretText = readVariable(provider, "secret_text");

        // Read capacity-specific template variables
        this.removesCeilingText = readVariable(provider, "removes_ceiling_text");

        // Other variables are read in the process()
    }

    @Override
    public String process(String key) {

        int index = ProcessorUtils.splitKeyIndex(key);

        // Caching avoids re-computing of all fields when a single brew has multiple fields
        // In this case we have stack and description
        BrewActionInfo info = null;
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
                info = new BrewActionInfo(ItemStackUtil.serializeStack(this.stack), this.description);
                cache.put(index, info);
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
        this.removesCeiling = false;
        obfuscate(this.removesCeilingText, EObfuscationMethod.MINECRAFT);
        super.obfuscateFields();
    }

    @Override
    protected void hideFields() {
        this.removesCeiling = false;
        this.removesCeilingText = "";
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
                sb.append(this.secretText);
            if (this.removesCeiling) {
                if (this.isSecret) sb.append(", ");
                sb.append(this.removesCeilingText);
            }
            sb.append(")");
        }

        return sb.toString();
    }

    public static void clearCache() {
        cache.clear();
    }

}
