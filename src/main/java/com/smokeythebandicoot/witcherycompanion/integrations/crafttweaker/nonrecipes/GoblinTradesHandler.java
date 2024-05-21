package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes;

import com.smokeythebandicoot.witcherycompanion.integrations.api.GoblinTradeApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.GoblinTrade")
@ZenRegister
public class GoblinTradesHandler {

    @ZenMethod
    @ZenDoc("")
    public static boolean registerProfession(String name) {
        return GoblinTradeApi.registerProfession(name);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean unregisterProfession(String name) {
        return GoblinTradeApi.unregisterProfession(name);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean setProfessionFallbackTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.setProfessionFallbackTrade(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell, int level) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s, level);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell, float chance) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s, chance);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell, float chance, int level) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s, chance, level);
    }

}
