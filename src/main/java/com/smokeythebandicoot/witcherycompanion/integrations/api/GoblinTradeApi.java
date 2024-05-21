package com.smokeythebandicoot.witcherycompanion.integrations.api;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is responsible to Bridge between Witchery and Integrations (Crafttweaker, Groovy (eventually))
 * It keeps track of the Goblin trades and provides them to Witchery. Integrations can modify this class
 * Mixins will inject the trades contained here in the EntityGoblin class
 */
public class GoblinTradeApi {

    private static final List<GoblinProfession> professions = new ArrayList<>();

    /** =============================== TRADE GENERATION =============================== **/
    public static MerchantRecipeList getTrades(int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.getAllTrades(Integer.MAX_VALUE);
    }

    public static MerchantRecipeList generateTrades(int professionId, int toLevel) {
        GoblinProfession profession = professions.get(professionId);
        return profession.getAllTrades(toLevel);
    }

    /** =============================== PROFESSION RETRIEVAL =============================== **/
    public static int getRandomProfessionID(World world) {
        if (professions.isEmpty()) return -1;
        return world.rand.nextInt(professions.size());
    }

    public static GoblinProfession getRandomProfession(World world) {
        return professions.get(getRandomProfessionID(world));
    }

    public static GoblinProfession getProfessionByName(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                return profession;
            }
        }
        return null;
    }

    public static List<String> listProfessions() {
        List<String> list = new ArrayList<>();
        for (GoblinProfession profession : professions) {
            list.add(profession.professionName);
        }
        return list;
    }

    /** =============================== PROFESSION MANIP =============================== **/
    /** Registers a new profession with a name and no fallback trade. Returns false if a profession with that name
     already exists */
    public static boolean registerProfession(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) return false;
        }
        GoblinProfession newProfession = new GoblinProfession(professionName, null);
        professions.add(newProfession);
        return true;
    }

    /** Un-registers a previously-registered profession. Returns false if the profession did not exist */
    public static boolean unregisterProfession(String professionName) {
        GoblinProfession toRemove = null;
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                toRemove = profession;
            }
        }
        if (toRemove == null) return false;
        professions.remove(toRemove);
        return true;
    }

    /** =============================== TRADES MANIP =============================== **/
    /** Sets the fallback trade for this profession. Returns false if the profession does not exist */
    public static boolean setProfessionFallbackTrade(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.fallBackTrade = new GoblinTrade(buy1, buy2, sell, 1.0f, 0);
        return true;
    }

    /** Removes the fallback trade for this profession. Returns false if the profession does not exist */
    public static boolean removeProfessionFallbackTrade(String professionName) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.fallBackTrade = null;
        return true;
    }

    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, 1.0f, 0));
        return true;
    }

    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell, float chance) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, chance, 0));
        return true;
    }

    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell, int level) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, 1.0f, level));
        return true;
    }

    public static boolean addTradeToProfession(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell, float chance, int level) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.addTrade(new GoblinTrade(buy1, buy2, sell, chance, level));
        return true;
    }

    public static boolean removeTrade(String professionName, ItemStack buy1, ItemStack buy2, ItemStack sell, float chance) {
        GoblinProfession profession = getProfessionByName(professionName);
        if (profession == null) return false;
        profession.removeTradeByMatching(buy1, buy2, sell, chance));
        return true;
    }






    public static class GoblinProfession {

        // The name of the profession
        public String professionName;

        // In case no trades are available, this is the fallback one
        public GoblinTrade fallBackTrade;

        // A list of possible trades and the probability to select one of them
        private final List<GoblinTrade> possibleTrades;

        public GoblinProfession(String professionName, GoblinTrade fallBackTrade) {
            this.fallBackTrade = fallBackTrade;
            this.professionName = professionName;
            possibleTrades = new ArrayList<>();
        }

        /** Adds a new trade for this profession */
        public void addTrade(GoblinTrade trade) {
            possibleTrades.add(trade);
        }

        /** Removes a trade */
        public void removeTrade(GoblinTrade trade) {
            possibleTrades.remove(trade);
        }

        /** Removes a trade that matches the elements. Use null for wildcard, use Items.AIR for empty stack */
        public void removeTradeByMatching(ItemStack buy1, ItemStack buy2, ItemStack sell, Float chance) {
            List<GoblinTrade> toRemove = new ArrayList<GoblinTrade>();
            for (GoblinTrade trade : possibleTrades) {
                MerchantRecipe t = trade.trade;

                // If trade matches, add it to removal list. Null is considered a wildcard, while Empty itemstack
                // matches an empty item in the trade (Buy 1 + Empty -> Sell)
                if ((buy1 == null || buy1 == t.getItemToBuy()) &&
                    (buy2 == null || buy2 == t.getSecondItemToBuy()) &&
                    (sell == null || sell == t.getItemToSell()) &&
                    (chance == null || chance == trade.probability)
                ) {
                    toRemove.add(trade);
                }
            }

            for (GoblinTrade trade : toRemove) {
                possibleTrades.remove(trade);
            }
        }

        /** Generates a list of trades based on current random context and the list of current possible trades. Use
         Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList generateActualTrades(World world) {
            // Init random generator
            Random random;
            if (world == null) random = new Random();
            else random = world.rand;

            // Init result list
            MerchantRecipeList tradeList = new MerchantRecipeList();

            for (GoblinTrade trade : possibleTrades) {
                if (random.nextFloat() < trade.probability) {
                    tradeList.add(trade.trade);
                }
            }

            // Check fallback
            if (tradeList.isEmpty() && fallBackTrade != null) {
                tradeList.add(fallBackTrade.trade);
            }

            return tradeList;
        }

        /** Returns all trades that are possible with this profession. Use Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList getAllTrades() {
            MerchantRecipeList tradeList = new MerchantRecipeList();
            for (GoblinTrade trade : possibleTrades) {
                tradeList.add(trade.trade);
            }
            if (fallBackTrade != null) tradeList.add(fallBackTrade.trade);
            return tradeList;
        }


    }

    public static class GoblinTrade {

        public final MerchantRecipe trade;
        public final float probability;

        public GoblinTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, Float probability) {

            this.probability = probability == null ? 1.0f : probability;
            if (buy1 == null) buy1 = new ItemStack(Items.AIR);
            if (buy2 == null) buy2 = new ItemStack(Items.AIR);
            if (sell == null) sell = new ItemStack(Items.AIR);
            this.trade = new MerchantRecipe(buy1, buy2, sell);

        }

    }


}
