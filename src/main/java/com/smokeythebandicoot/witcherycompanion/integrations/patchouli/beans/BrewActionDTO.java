package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.brewing.action.BrewAction;

public abstract class BrewActionDTO extends AbstractDTO {

    public boolean isSecret;
    public String brewName;
    public String brewType;
    public ItemStack stack;

    public BrewActionDTO(BrewAction action) {
        this.stack = action.getKey().toStack();
        this.isSecret = action.getHidden();
        if (action.getNamePart() != null)
            this.brewName = action.getNamePart().toString();
        else
            this.brewName = "<no name>";
        this.brewType = "<brew type>";
    }

    @Override
    public String getSecretKey() {
        return ProgressUtils.getBrewActionSecret(this.stack);
    }
}
