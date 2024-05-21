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
    @ZenDoc("Registers a new profession. At least on profession must exist in order for Hobgoblins to have trades. Takes in " +
            "the name of the profession, returns true if the registration was successfull")
    public static boolean registerProfession(String name) {
        return GoblinTradeApi.registerProfession(name);
    }

    @ZenMethod
    @ZenDoc("Un-registers a profession. Takes a profession name and returns true if the profession was removed correctly. If the " +
            "profession did not exist, returns false.")
    public static boolean unregisterProfession(String name) {
        return GoblinTradeApi.unregisterProfession(name);
    }

    @ZenMethod
    @ZenDoc("Sets the specified profession's fallback trade. The fallback trade is a trade that the profession gives when no trades " +
            "have been generated for the goblin. This ensures that the Goblin has at least one valid trade.")
    public static boolean setProfessionFallbackTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.setProfessionFallbackTrade(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("Sets the specified profession's fallback trade. The fallback trade is a trade that the profession gives when no trades " +
            "have been generated for the goblin. This ensures that the Goblin has at least one valid trade. Returns true on success")
    public static boolean setProfessionFallbackTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.setProfessionFallbackTrade(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("Removes the specified profession's fallback trade, meaning that the Hobgoblin won't have any trade if no trades are " +
            "generated for its profession. Returns true on success")
    public static boolean removeProfessionFallbackTrade(String profession) {
        return GoblinTradeApi.setProfessionFallbackTrade(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("Adds a trade to the specified profession. Multiple duplicate trades can be added, but they cannot be distinguished for " +
            "successive removal. Returns true if the trade was added successfully. First item and sell item cannot be empty")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        if (b1.isEmpty() || s.isEmpty()) return false;
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s);
    }

    @ZenMethod
    @ZenDoc("Adds a trade to the specified profession. Multiple duplicate trades can be added, but they cannot be distinguished for " +
            "successive removal. Returns true if the trade was added successfully. First item and sell item cannot be empty.")
    public static boolean addTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell, Float chance) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        if (b1.isEmpty() || s.isEmpty()) return false;
        return GoblinTradeApi.addTradeToProfession(profession, b1, b2, s, chance);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean removeTrade(String profession, IItemStack buy1, IItemStack buy2, IItemStack sell, Float chance) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.removeTrade(profession, b1, b2, s, chance);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean removeTradesByInput(String profession, IItemStack buy, Float chance) {
        ItemStack buy = CraftTweakerMC.getItemStack(buy1);
        return GoblinTradeApi.removeTrade(profession, buy, null, null, chance == -1 ? null : chance) ||
                GoblinTradeApi.removeTrade(profession, null, buy, null, chance == -1 ? null : chance);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean removeTradesByInputs(String profession, IItemStack buy1, IItemStack buy2, Float chance) {
        ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
        ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
        return GoblinTradeApi.removeTrade(profession, b1, b2, null, chance == -1 ? null : chance) ||
                GoblinTradeApi.removeTrade(profession, b2, b1, null, chance == -1 ? null : chance);
    }

    @ZenMethod
    @ZenDoc("")
    public static boolean addTradesByOutput(String profession, IItemStack sell, Float chance, Integer level) {
        ItemStack s = CraftTweakerMC.getItemStack(sell);
        return GoblinTradeApi.addTradeToProfession(profession, null, null, s, chance, level);
    }



}
