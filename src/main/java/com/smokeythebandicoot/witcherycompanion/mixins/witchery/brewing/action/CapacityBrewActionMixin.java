package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing.action;

import com.smokeythebandicoot.witcherycompanion.api.brewing.ICapacityBrewActionAccessor;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CapacityBrewAction.class)
public abstract class CapacityBrewActionMixin implements ICapacityBrewActionAccessor {

    @Shadow(remap = false) @Final
    private boolean removeCeiling;

    @Override
    public boolean getRemoveCeiling() {
        return this.removeCeiling;
    }

}
