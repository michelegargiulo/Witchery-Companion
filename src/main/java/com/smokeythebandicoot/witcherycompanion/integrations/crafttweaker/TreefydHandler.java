package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.TreefydApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Treefyd")
@ZenRegister
public class TreefydHandler {

    @ZenMethod
    @ZenDoc(value="Registers a Spinning Wheel Recipe")
    public static IItemDefinition getLevel1BoostItem() {
        ItemStack stack = new ItemStack(TreefydApi.getLevel1BoostItem());
        return CraftTweakerMC.getIItemStack(stack).getDefinition();
    }

    @ZenMethod
    @ZenDoc(value="Registers a Spinning Wheel Recipe")
    public static void setLevel1BoostItem(IItemDefinition itemDefinition) {
        Item item = CraftTweakerMC.getItem(itemDefinition);
        TreefydApi.setLevel1BoostItem(item);
    }

    @ZenMethod
    @ZenDoc(value="Registers a Spinning Wheel Recipe")
    public static IItemDefinition getLevel2BoostItem() {
        ItemStack stack = new ItemStack(TreefydApi.getLevel2BoostItem());
        return CraftTweakerMC.getIItemStack(stack).getDefinition();
    }

    @ZenMethod
    @ZenDoc(value="Registers a Spinning Wheel Recipe")
    public static void setLevel2BoostItem(IItemDefinition itemDefinition) {
        Item item = CraftTweakerMC.getItem(itemDefinition);
        TreefydApi.setLevel2BoostItem(item);
    }

}
