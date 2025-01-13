package com.smokeythebandicoot.witcherycompanion.api.accessors.rite;

import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;

import java.util.List;

public interface IItemRiteSacrificeAccessor {

    List<ItemRiteSacrifice.ItemRequirement> getRequirements();

}
