package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.*;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
import java.util.Map;


public class DispersalBrewActionProcessor extends BrewActionProcessor {

    protected String instantDescription;
    protected String liquidDescription;
    protected String gasDescription;
    protected String triggeredDescription;
    protected String customDescription;

    protected Ingredient ingredient;
    protected String dispersalType;
    protected Class<? extends Dispersal> dispersalClass;

    private static final Map<Class<? extends Dispersal>, BrewActionInfo> cache = new HashMap<>();


    // We override the setup because we do not have a one-brew-per-page case, but we have a
    // list of all the capacity items in the same page. DTO is set dynamically, and we do not have
    // a single CapacityBrewAction to point to.
    @Override
    public void setup(IVariableProvider<String> provider) {

        this.instantDescription = readVariable(provider, "instant_description");
        this.liquidDescription = readVariable(provider, "liquid_description");
        this.gasDescription = readVariable(provider, "gas_description");
        this.triggeredDescription = readVariable(provider, "triggered_description");
        this.customDescription = readVariable(provider, "custom_description");

        // Other variables are read in the process()
    }

    @Override
    public String process(String key) {

        String[] keysplit = key.split("@");
        if (keysplit.length != 2) return null; // Invalid key format

        this.dispersalClass = getDispersalByName(keysplit[1]);
        if (this.dispersalClass == null) return null; // No dispersal found

        String actualKey = keysplit[0];
        BrewActionInfo info = null;
        if (cache.containsKey(this.dispersalClass)) {
            info = cache.get(this.dispersalClass);
        }
        else {

            this.ingredient = BrewRegistry.getDispersalIngredients(dispersalClass);
            if (ingredient != null) {

                // Variables from superclass
                this.isSecret = false; // No secret dispersals. Harder to handle with ingredients

                // Description (at the end because other fields need to be set)
                this.description = getDescription(this.dispersalClass);

                // We ignore brewName and brewType, as we do not use them in this template

                // Call obfuscate fields
                obfuscateIfSecret();

                // Now that we have all the info, we cache it into a CapacityBrewActionInfo
                info = new BrewActionInfo(ItemStackUtil.serializeIngredient(ingredient), this.description);
                cache.put(this.dispersalClass, info);
            }
        }

        if (info == null) return null;

        switch (actualKey) {
            case "stack":
                return info.serializedStack;
            case "description":
                return info.description;
        }

        // This does not call super, and is_enabled always returns true, as it is the BrewRegistry itself
        // that adds or removes components
        return null;
    }

    protected Class<? extends Dispersal> getDispersalByName(String dispersalName) {
        if (dispersalName == null)
            return null;

        switch (dispersalName) {
            case "instant":
                return InstantDispersal.class;
            case "liquid":
                return LiquidDispersal.class;
            case "gas":
                return GasDispersal.class;
            case "triggered":
                return TriggeredDispersal.class;
            default:
                try {
                    Class<?> c = Class.forName(dispersalName);
                    if (Dispersal.class.isAssignableFrom(c)) {
                        return (Class<? extends Dispersal>) c;
                    }
                } catch (ClassNotFoundException ignored) { }
        }

        return null;
    }

    public String getDescription(Class<? extends Dispersal> dispersal) {
        if (dispersal == InstantDispersal.class)
            return this.instantDescription;
        else if (dispersal == LiquidDispersal.class)
            return this.liquidDescription;
        else if (dispersal == GasDispersal.class)
            return this.gasDescription;
        else if (dispersal == TriggeredDispersal.class)
            return this.triggeredDescription;
        else
            return this.customDescription;
    }

    public static void clearCache() {
        cache.clear();
    }


}
