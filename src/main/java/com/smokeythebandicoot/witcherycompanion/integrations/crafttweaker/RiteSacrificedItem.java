package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.customrite.CustomRiteContext;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/** An item sacrificed in a rite, as seen by ZenScript */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.RiteSacrificedItem")
@ZenRegister
public class RiteSacrificedItem {

    private final CustomRiteContext.SacrificedItem item;

    public RiteSacrificedItem(CustomRiteContext.SacrificedItem item) {
        this.item = item;
    }

    @ZenGetter("item")
    @ZenDoc(value = "The sacrificed item (one unit)")
    public IItemStack getItem() {
        return CraftTweakerMC.getIItemStack(item.getStack());
    }

    @ZenGetter("position")
    @ZenDoc(value = "Position where the item was consumed")
    public IBlockPos getPosition() {
        return CraftTweakerMC.getIBlockPos(item.getPosition());
    }

    @ZenGetter("identifier")
    @ZenDoc(value = "Numeric key that W:R uses for the item (derived from the identifier string)")
    public long getIdentifier() {
        return item.getIdentifier();
    }

}
