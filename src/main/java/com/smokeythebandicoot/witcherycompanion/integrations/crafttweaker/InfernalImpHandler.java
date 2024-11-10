package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.InfernalImp")
@ZenRegister
public class InfernalImpHandler {

    @ZenMethod
    @ZenDoc("Adds a new shiny with the defined affection boost. Affection boost must be positive or item won't be " +
            "registered")
    public static boolean addShiny(IItemStack iItemStack, int affectionBoost) {
        ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
        return InfernalImpApi.addShiny(stack, affectionBoost);
    }

    @ZenMethod
    @ZenDoc("Removes a shiny from the shiny list. Imp won't consider the item valuable anymore")
    public static boolean removeShiny(IItemStack iItemStack) {
        ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
        return InfernalImpApi.removeShiny(stack);
    }

    @ZenMethod
    @ZenDoc("Returns true if the given item is a shiny")
    public static boolean isShiny(IItemStack iItemStack) {
        ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
        return InfernalImpApi.isShiny(stack);
    }

    @ZenMethod
    @ZenDoc("Returns the given item affection boost. Returns 0 if the item is not a shiny")
    public static int getAffectionBoost(IItemStack iItemStack) {
        ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
        return InfernalImpApi.getAffectionBoost(stack);
    }

    @ZenMethod
    @ZenDoc("Returns the set gift that has been set for the specified index. " +
            "Returns null if no fixed gift is set for the index")
    public static IItemStack getGift(int secretIndex) {
        ItemStack stack = InfernalImpApi.getGift(secretIndex);
        return CraftTweakerMC.getIItemStack(stack);
    }

    @ZenMethod
    @ZenDoc("Sets the provided IItemStack as the given item for the index")
    public static void setGift(IItemStack iItemStack, int secretIndex) {
        ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
        InfernalImpApi.setGift(stack, secretIndex);
    }

}
