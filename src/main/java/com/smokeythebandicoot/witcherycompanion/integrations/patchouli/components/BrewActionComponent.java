package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.BrewActionDTO;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components.base.BaseComponent;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.common.util.ItemStackUtil;


public abstract class BrewActionComponent<T extends BrewActionDTO> extends BaseComponent<T> {

    @SerializedName("stack")
    @VariableHolder
    public String _stack;

    protected transient ItemStack stack = null;

    @Override
    protected void onBuild() {
        this.stack = getTransform(this._stack, ItemStackUtil::loadStackFromString, dto, d -> d.stack, ItemStack.EMPTY);
    }
}
