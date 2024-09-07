package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.api.brewing.ICapacityBrewActionAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import vazkii.patchouli.common.util.ItemStackUtil;

public class CapacityBrewActionDTO extends BrewActionDTO {

    // Set by brew
    @Expose public String increment = "+0";
    @Expose public boolean removesCeiling = false;

    // From template
    @Expose public String removesCeilingText = "";
    @Expose public String removesCeilingTooltip = "";

    @Override
    protected void initFields() {
        super.initFields();

        mapField("increment",
                str -> this.increment = str,
                () -> obfuscateIfSecret(this.increment, EObfuscationMethod.PATCHOULI)
        );

        mapField("removes_ceiling",
                str -> this.removesCeiling = Boolean.parseBoolean(str),
                () -> String.valueOf(this.removesCeiling)
        );

        mapField("removes_ceiling_text",
                str -> this.removesCeilingText = str,
                () -> obfuscateIfSecret(this.removesCeilingText, EObfuscationMethod.PATCHOULI)
        );

        mapField("removes_ceiling_tooltip",
                str -> this.removesCeilingTooltip = str,
                () -> obfuscateIfSecret(this.removesCeilingTooltip, EObfuscationMethod.MINECRAFT)
        );
    }


    public CapacityBrewActionDTO() { }

    public CapacityBrewActionDTO(CapacityBrewAction action) {
        this.stack = action.getKey().toStack();
        this.increment = "+" + action.getIncrement();

        if ((Object)action instanceof ICapacityBrewActionAccessor) {
            ICapacityBrewActionAccessor accessor = (ICapacityBrewActionAccessor) (Object) action;
            this.removesCeiling = accessor.getRemoveCeiling();
        } else {
            this.removesCeiling = false;
        }

        this.isSecret = action.getHidden();
    }


    @Override
    public String getForKey(String key) {
        // Strips the last digit-only char sequence at the end of the string
        String indexlessKey = key.replaceAll("[0-9]+$", "");
        return super.getForKey(indexlessKey);
    }

    @Override
    public String getSecretKey() {
        if (this.stack == null) return null;
        return "brewing/items/" + ItemStackUtil.serializeStack(this.stack);
    }
}
