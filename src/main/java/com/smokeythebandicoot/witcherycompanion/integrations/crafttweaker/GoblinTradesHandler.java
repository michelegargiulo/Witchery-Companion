package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.GoblinTradeApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashSet;
import java.util.Set;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.GoblinTrading")
@ZenRegister
public class GoblinTradesHandler {

    @ZenMethod
    @ZenDoc("Retrieve a profession by name. Returns null if it does not exist")
    public static GoblinProfessionWrapper getProfession(String name) {
        GoblinTradeApi.GoblinProfession profession = GoblinTradeApi.getProfessionByName(name);
        return profession == null ? null : new GoblinProfessionWrapper(profession);
    }

    @ZenMethod
    @ZenDoc("Registers a new profession. If registration was successful, ")
    public static GoblinProfessionWrapper registerProfession(String name) {
        GoblinTradeApi.GoblinProfession profession = GoblinTradeApi.registerProfession(name);
        return profession == null ? null : new GoblinProfessionWrapper(profession);
    }

    @ZenMethod
    @ZenDoc("Un-registers a profession. Takes a profession name and returns true if the profession was removed correctly. If the " +
            "profession did not exist, returns false.")
    public static GoblinProfessionWrapper unregisterProfession(String name) {
        GoblinTradeApi.GoblinProfession profession = GoblinTradeApi.unregisterProfession(name);
        return profession == null ? null : new GoblinProfessionWrapper(profession);
    }

    @ZenMethod
    @ZenDoc("Returns an array of registered profession names (strings).")
    public static String[] listProfessionNames() {
        return GoblinTradeApi.listProfessionsNames().toArray(new String[0]);
    }

    @ZenMethod
    @ZenDoc("Returns an array of registered profession names (strings).")
    public static GoblinProfessionWrapper[] listProfessions() {
        return GoblinTradeApi.listProfessions().stream()
                .map(GoblinProfessionWrapper::new)
                .toArray(GoblinProfessionWrapper[]::new);
    }


    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.GoblinProfession")
    public static class GoblinProfessionWrapper {

        private final GoblinTradeApi.GoblinProfession profession;

        public GoblinProfessionWrapper(GoblinTradeApi.GoblinProfession profession) {
            this.profession = profession;
        }

        @ZenMethod
        @ZenDoc("Returns the name of the profession")
        public String getName() {
            return profession.getName();
        }

        /** ================= FALLBACK TRADES ================= **/
        @ZenMethod
        @ZenDoc("Returns the fallback trade of the profession. Can return null")
        public GoblinTradeWrapper getFallbackTrade() {
            return new GoblinTradeWrapper(profession.getFallBackTrade());
        }

        @ZenMethod
        @ZenDoc("Sets the fallback trade of the profession. Can be null")
        public GoblinTradeWrapper setFallbackTrade(GoblinTradeWrapper trade) {
            if (trade == null) {
                profession.setFallbackTrade(null);
                return null;
            }
            return new GoblinTradeWrapper(profession.setFallbackTrade(trade.getTrade()));
        }

        @ZenMethod
        @ZenDoc("Sets the fallback trade of the profession. Returns the added fallback trade. Chance defaults to 1.0 (100%)")
        public GoblinTradeWrapper setFallbackTrade(IItemStack buy1, IItemStack buy2, IItemStack sell) {
            ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
            ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
            ItemStack s = CraftTweakerMC.getItemStack(sell);
            return new GoblinTradeWrapper(profession.setFallbackTrade(b1, b2, s));
        }


        /** ================= TRADES ================= **/
        @ZenMethod
        @ZenDoc("Adds a new Trade to the profession. Chance defaults to 1.0 (100%)")
        public GoblinTradeWrapper addTrade(IItemStack buy1, IItemStack buy2, IItemStack sell) {
            ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
            ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
            ItemStack s = CraftTweakerMC.getItemStack(sell);
            return new GoblinTradeWrapper(profession.addTrade(b1, b2, s));
        }

        @ZenMethod
        @ZenDoc("Adds a new Trade to the profession")
        public GoblinTradeWrapper addTrade(IItemStack buy1, IItemStack buy2, IItemStack sell, float chance) {
            ItemStack b1 = CraftTweakerMC.getItemStack(buy1);
            ItemStack b2 = CraftTweakerMC.getItemStack(buy2);
            ItemStack s = CraftTweakerMC.getItemStack(sell);
            return new GoblinTradeWrapper(profession.addTrade(b1, b2, s, chance));
        }

        @ZenMethod
        @ZenDoc("Adds a new Trade to the profession")
        public GoblinTradeWrapper addTrade(GoblinTradeWrapper trade) {
            profession.addTrade(trade.getTrade());
            return trade;
        }

        @ZenMethod
        @ZenDoc("Removes the specified trade from the profession")
        public GoblinTradeWrapper removeTrade(GoblinTradeWrapper trade) {
            GoblinTradeApi.GoblinTrade t = profession.removeTrade(trade.getTrade());
            return t == null ? null : new GoblinTradeWrapper(t);
        }

        @ZenMethod
        @ZenDoc("Removes from the profession all trades that have the have the two input item " +
                "matching both of the input IIngredients. Order matters, and null can be used as a wildcard. " +
                "The trade must also match the output IIngredient, and null can be used as a wildcard. " +
                "Chance is ignored")
        public GoblinTradeWrapper[] removeMatchingTrades(IIngredient buy1, IIngredient buy2, IIngredient sell) {
            Ingredient b1 = CraftTweakerMC.getIngredient(buy1);
            Ingredient b2 = CraftTweakerMC.getIngredient(buy2);
            Ingredient s = CraftTweakerMC.getIngredient(sell);
            Set<GoblinTradeApi.GoblinTrade> removedTrades = new HashSet<>(
                    profession.removeTradeByMatching(b1, b2, s, null));
            return removedTrades.stream().map(GoblinTradeWrapper::new).toArray(GoblinTradeWrapper[]::new);
        }

        @ZenMethod
        @ZenDoc("Removes from the profession all trades that have the have the two input item " +
                "matching both of the input IIngredients. Order matters, and null can be used as a wildcard. " +
                "The trade must also match the output IIngredient, and null can be used as a wildcard. " +
                "Chance has to match")
        public GoblinTradeWrapper[] removeMatchingTrades(IIngredient buy1, IIngredient buy2, IIngredient sell, float chance) {
            Ingredient b1 = CraftTweakerMC.getIngredient(buy1);
            Ingredient b2 = CraftTweakerMC.getIngredient(buy2);
            Ingredient s = CraftTweakerMC.getIngredient(sell);
            Set<GoblinTradeApi.GoblinTrade> removedTrades = new HashSet<>(
                    profession.removeTradeByMatching(b1, b2, s, chance));
            return removedTrades.stream().map(GoblinTradeWrapper::new).toArray(GoblinTradeWrapper[]::new);
        }
    }


    @ZenRegister
    @ZenClass("mods.smokeythebandicoot.witcherycompanion.GoblinTrade")
    public static class GoblinTradeWrapper {

        private final GoblinTradeApi.GoblinTrade trade;

        public GoblinTradeWrapper(GoblinTradeApi.GoblinTrade trade) {
            this.trade = trade;
        }

        public GoblinTradeApi.GoblinTrade getTrade() {
            return trade;
        }

        @ZenMethod
        @ZenDoc("Returns the first item to buy")
        public IItemStack getFirstItem() {
            return CraftTweakerMC.getIItemStack(trade.getTrade().getItemToBuy());
        }

        @ZenMethod
        @ZenDoc("Returns the second item to buy")
        public IItemStack getSecondItem() {
            return CraftTweakerMC.getIItemStack(trade.getTrade().getSecondItemToBuy());
        }

        @ZenMethod
        @ZenDoc("Returns the item to sell")
        public IItemStack getSellItem() {
            return CraftTweakerMC.getIItemStack(trade.getTrade().getItemToSell());
        }

        @ZenMethod
        @ZenDoc("Returns the chance that the trade appears in a Goblin that has this profession")
        public float getChance() {
            return trade.getChance();
        }

    }
}
