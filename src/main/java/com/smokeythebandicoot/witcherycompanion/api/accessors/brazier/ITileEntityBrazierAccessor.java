package com.smokeythebandicoot.witcherycompanion.api.accessors.brazier;

import net.minecraft.entity.player.EntityPlayer;

public interface ITileEntityBrazierAccessor {

    EntityPlayer getRecipeOwner();

    void setRecipeOwner(EntityPlayer player);

}
