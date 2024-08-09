package com.smokeythebandicoot.witcherycompanion.api.goblintrade;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible to Bridge between Witchery and Integrations (Crafttweaker, Groovy (eventually))
 * It keeps track of the Goblin trades and provides them to Witchery. Integrations can modify this class
 * Mixins will inject the trades contained here in the EntityGoblin class
 */
public class GoblinTradeApi {

    private static final List<GoblinProfession> professions = new ArrayList<>();

    /** =============================== TRADE GENERATION =============================== **/
    /** Generates random trades for this profession. Creates a new instance of Random */
    public static MerchantRecipeList generateTrades(int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.generateActualTrades(null);
    }

    /** Generates random trades for this profession. Uses world's random */
    public static MerchantRecipeList generateTrades(World world, int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.generateActualTrades(world);
    }

    /** Returns a MerchantRecipeList that represents ALL trades (ignores chances) */
    public static MerchantRecipeList getTrades(int professionId) {
        GoblinProfession profession = professions.get(professionId);
        return profession.getAllTrades();
    }

    /** =============================== PROFESSION RETRIEVAL =============================== **/
    /** Generates a random integer, valid as a profession ID */
    public static int getRandomProfessionID(Random random) {
        if (professions.isEmpty()) return -1;
        return random.nextInt(professions.size());
    }

    /** Generates a random profession among the valid registered ones */
    public static GoblinProfession getRandomProfession(Random random) {
        return professions.get(getRandomProfessionID(random));
    }

    /** Retrieves a GoblinProfession object by name */
    public static GoblinProfession getProfessionByName(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                return profession;
            }
        }
        return null;
    }

    /** Retrieves a GoblinProfession object by ID */
    public static GoblinProfession getProfessionByID(int ID) {
        if (professions.size() > ID)
            return professions.get(ID);
        return null;
    }

    /** Returns the number of registered professions */
    public static int getProfessionCount() {
        return professions.size();
    }

    /** Generates a list of profession names */
    public static List<String> listProfessionsNames() {
        return professions.stream().map(profession -> profession.professionName).collect(Collectors.toList());
    }

    /** Generates a list of GoblinProfession (copy, cannot modify registered professions) */
    public static List<GoblinProfession> listProfessions() {
        return new ArrayList<>(professions);
    }

    /** Registers a new profession with a name and no fallback trade. On success, returns
     * the newly created profession. Returns null otherwise (for example, a profession with
     * that name already exists) */
    public static GoblinProfession registerProfession(String professionName) {
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName))
                return null;
        }
        GoblinProfession newProfession = new GoblinProfession(professionName, null);
        professions.add(newProfession);
        return newProfession;
    }

    /** Un-registers a previously-registered profession. Returns false if the profession did not exist */
    public static GoblinProfession unregisterProfession(String professionName) {
        GoblinProfession toRemove = null;
        for (GoblinProfession profession : professions) {
            if (profession.professionName.equals(professionName)) {
                toRemove = profession;
            }
        }
        if (toRemove == null) return null;
        professions.remove(toRemove);
        return toRemove;
    }

    public static class GoblinProfession {

        // The name of the profession
        private final String professionName;

        // In case no trades are available, this is the fallback one
        private GoblinTrade fallBackTrade;

        // A list of possible trades and the probability to select one of them
        private final List<GoblinTrade> possibleTrades;

        public GoblinProfession(String professionName, GoblinTrade fallBackTrade) {
            this.fallBackTrade = fallBackTrade;
            this.professionName = professionName;
            possibleTrades = new ArrayList<>();
        }

        public String getName() {
            return professionName;
        }

        /** Returns the fallback trade for this profession */
        public GoblinTrade getFallBackTrade() {
            return this.fallBackTrade;
        }

        /** Sets the fallback trade for this profession */
        public GoblinTrade setFallbackTrade(ItemStack buy1, ItemStack buy2, ItemStack sell) {
            this.fallBackTrade = new GoblinTrade(buy1, buy2, sell, 1.0f);
            return this.fallBackTrade;
        }

        /** Sets the fallback trade for this profession */
        public GoblinTrade setFallbackTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, float chance) {
            this.fallBackTrade = new GoblinTrade(buy1, buy2, sell, chance);
            return this.fallBackTrade;
        }

        /** Sets the fallback trade for this profession */
        public GoblinTrade setFallbackTrade(GoblinTrade trade) {
            this.fallBackTrade = trade;
            return this.fallBackTrade;
        }

        /** Adds a new trade for this profession */
        public GoblinTrade addTrade(GoblinTrade trade) {
            possibleTrades.add(trade);
            return trade;
        }

        /** Adds a new trade for this profession */
        public GoblinTrade addTrade(ItemStack buy1, ItemStack buy2, ItemStack sell) {
            GoblinTrade trade = new GoblinTrade(buy1, buy2, sell, 1.0f);
            possibleTrades.add(trade);
            return trade;
        }

        /** Adds a new trade for this profession */
        public GoblinTrade addTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, float chance) {
            GoblinTrade trade = new GoblinTrade(buy1, buy2, sell, chance);
            possibleTrades.add(trade);
            return trade;
        }

        /** Removes a trade */
        public GoblinTrade removeTrade(GoblinTrade trade) {
            if (possibleTrades.remove(trade)) {
                return trade;
            }
            return null;
        }

        /** Removes a trade that matches the inputs, output and chance. Use null for wildcard, use Items.AIR for empty stack */
        public List<GoblinTrade> removeTradeByMatching(Ingredient buy1, Ingredient buy2, Ingredient sell, Float chance) {
            List<GoblinTrade> toRemove = new ArrayList<>();
            for (GoblinTrade trade : possibleTrades) {
                MerchantRecipe t = trade.getTrade();
                // If the trade matches, add it to removal list. Null is considered a wildcard, while Empty itemstack
                // matches an empty item in the trade (Buy 1 + Empty -> Sell).
                if ((buy1 == null || buy1.apply(t.getItemToBuy())) &&
                    (buy2 == null || buy2.apply(t.getSecondItemToBuy())) &&
                    (sell == null || sell.apply(t.getItemToSell())) &&
                    (chance == null || chance == trade.getChance())
                ) { // Add to the list of trades to remove
                    toRemove.add(trade);
                }
            }

            for (GoblinTrade trade : toRemove) {
                possibleTrades.remove(trade);
            }

            return toRemove;
        }

        /** Returns all trades that are possible with this profession. Use Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList getAllTrades() {
            MerchantRecipeList tradeList = new MerchantRecipeList();
            for (GoblinTrade trade : possibleTrades) {
                tradeList.add(trade.genNewTrade());
            }
            if (fallBackTrade != null) tradeList.add(fallBackTrade.genNewTrade());
            return tradeList;
        }

        /** Generates a list of trades based on current random context and the list of current possible trades. Use
         Integer.MAX_VALUE to get all trades */
        public MerchantRecipeList generateActualTrades(World worldIn) {
            // Init random generator
            Random random;
            if (worldIn == null) random = new Random();
            else random = worldIn.rand;

            // Init result list
            MerchantRecipeList tradeList = new MerchantRecipeList();

            // Copy and shuffle the trades
            List<GoblinTrade> tradePool = new ArrayList<>(possibleTrades);
            Collections.shuffle(tradePool);

            // Add generated recipes depending on chance and configuration
            for (GoblinTrade trade : tradePool) {
                if (random.nextFloat() < trade.getChance() &&
                        tradeList.size() < EntityTweaks.goblin_maxTradesPerLevel) {
                    tradeList.add(trade.genNewTrade());
                }
            }

            // Shuffle and trim the collection
            Collections.shuffle(tradeList);

            // Check fallback
            if (tradeList.isEmpty() && fallBackTrade != null) {
                tradeList.add(fallBackTrade.genNewTrade());
            }

            return tradeList;
        }


    }

    public static class GoblinTrade {

        private final MerchantRecipe trade;
        private final float probability;

        public GoblinTrade(ItemStack buyStack1, ItemStack buyStack2, ItemStack sellStack, Float probability) {

            this.probability = probability == null ? 1.0f : Math.min(Math.max(0.0f, probability), 1.0f);
            if (buyStack1 == null) buyStack1 = new ItemStack(Items.AIR);
            if (buyStack2 == null) buyStack2 = new ItemStack(Items.AIR);
            if (sellStack == null) sellStack = new ItemStack(Items.AIR);
            this.trade = new MerchantRecipe(buyStack1, buyStack2, sellStack);

        }

        public MerchantRecipe getTrade() {
            return trade;
        }

        public MerchantRecipe genNewTrade() {
            return new MerchantRecipe(this.trade.getItemToBuy(), this.trade.getSecondItemToBuy(), this.trade.getItemToSell());
        }

        public float getChance() {
            return probability;
        }

    }


}
