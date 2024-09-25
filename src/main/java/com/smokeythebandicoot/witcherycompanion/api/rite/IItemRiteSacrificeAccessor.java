package com.smokeythebandicoot.witcherycompanion.api.rite;

import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;

import java.util.List;

public interface IItemRiteSacrificeAccessor {

    List<ItemRiteSacrifice.ItemRequirement> getRequirements();

}
