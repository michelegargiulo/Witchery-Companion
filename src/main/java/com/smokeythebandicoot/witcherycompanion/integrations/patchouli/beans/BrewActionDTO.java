package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.common.util.ItemStackUtil;

public abstract class BrewActionDTO extends AbstractDTO {

    @Expose
    public ItemStack stack = ItemStack.EMPTY;

    @Override
    protected void initFields() {
        mapField("brew_item", null,
                () -> ProcessorUtils.serializeDto(this));

        mapField("stack",
                str -> this.stack = ItemStackUtil.loadStackFromString(str),
                () -> ItemStackUtil.serializeStack(this.getHideState() == EHiddenState.CLEARTEXT ? this.stack : ItemStack.EMPTY)
        );
    }

}
