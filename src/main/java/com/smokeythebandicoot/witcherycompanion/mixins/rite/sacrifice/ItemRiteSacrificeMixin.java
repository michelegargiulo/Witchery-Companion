package com.smokeythebandicoot.witcherycompanion.mixins.rite.sacrifice;

import com.smokeythebandicoot.witcherycompanion.api.rite.IItemRiteSacrificeAccessor;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;
import net.msrandom.witchery.rite.sacrifice.RiteSacrifice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
import java.util.List;

@Mixin(ItemRiteSacrifice.class)
public abstract class ItemRiteSacrificeMixin extends RiteSacrifice implements IItemRiteSacrificeAccessor {

    @Shadow(remap = false) @Final
    private List<ItemRiteSacrifice.ItemRequirement> requirements;

    private ItemRiteSacrificeMixin(SacrificeSerializer<?> serializer) {
        super(serializer);
    }

    @Override
    public List<ItemRiteSacrifice.ItemRequirement> getRequirements() {
        return this.requirements;
    }
}
