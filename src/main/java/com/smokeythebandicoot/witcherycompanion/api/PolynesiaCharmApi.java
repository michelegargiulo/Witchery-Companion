package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import java.util.*;

public class PolynesiaCharmApi {

    private static final Map<Class<? extends EntityLiving>, AnimalTradesOld> animalTrades;

    static {
        animalTrades = new HashMap<>();

        // Common items available for all trades
        AnimalTradesOld entityLiving = new AnimalTradesOld();
        // All Entity Livings have one trade with one of those items, so trade chance is 100% and
        // the item is selected randomly from this list
        entityLiving.addGoods(
                new ItemStack(WitcheryIngredientItems.MANDRAKE_ROOT, 3),
                new ItemStack(WitcheryIngredientItems.BELLADONNA, 3),
                new ItemStack(WitcheryIngredientItems.ARTICHOKE, 3),
                new ItemStack(Blocks.SAPLING, 4, 0),
                new ItemStack(Blocks.SAPLING, 4, 1),
                new ItemStack(Blocks.SAPLING, 4, 2),
                new ItemStack(Blocks.SAPLING, 4, 3),
                new ItemStack(Blocks.REEDS, 2),
                new ItemStack(Blocks.CACTUS, 2),
                new ItemStack(Items.GOLD_NUGGET, 5),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(Items.BONE, 4),
                new ItemStack(Items.FLINT, 5),
                new ItemStack(WitcheryIngredientItems.DOG_TONGUE, 2),
                new ItemStack(Items.POTATO, 5),
                new ItemStack(Items.POISONOUS_POTATO, 2),
                new ItemStack(Items.CARROT, 5),
                new ItemStack(Items.CLAY_BALL, 10)
        );
        // Every living entity has a 3% chance to have an additional trade with Treefyd Seeds
        entityLiving.addGoods(0.03, new ItemStack(WitcheryIngredientItems.TREEFYD_SEEDS));
        animalTrades.put(EntityLiving. class,entityLiving);

        // PIG
        AnimalTradesOld pig = entityLiving.clone();
        pig.addCurrency(new ItemStack(Items.CARROT));
        pig.addCurrency(new ItemStack(Items.APPLE));
        pig.addCurrency(new ItemStack(Items.POTATO));

    }


    private static AnimalTradesOld getData(EntityLiving entity) {
        Class<?> current = entity.getClass();
        while (current != null) {
            if (animalTrades.containsKey(current)) {
                return animalTrades.get(current);
            }
            current = current.getSuperclass();
        }
        return null;
    }

    public static MerchantRecipeList generateTradesFor(EntityLiving entity) {
        AnimalTradesOld trades = getData(entity);
        if (trades != null) {
            return trades.generateTrades(entity.getEntityWorld().rand);
        }
        return null;
    }



    public static class AnimalTrades {

        public ArrayList<ItemStack> currencies;

        public ArrayList<ItemStack> fallbackGoods;

        public HashMap<ItemStack, Double> goods;


        public AnimalTrades() {
            currencies = new ArrayList<>();
            fallbackGoods = new ArrayList<>();
            goods = new HashMap<>();
        }

        public MerchantRecipeList generateTrades(Random rand) {
            /** Process starts with a list of items (FALLBACK items)
             * An item is randomly selected from the FALLBACK items and added to GOODS list
             * Treefyd seeds are added to the GOODS list with 3% chance
             *
             * For each animal:
             * - currencies are added to the CURRENCY list
             * - items are added to the GOODS list with certain probability
             */
        }

    }


    public static class AnimalTradesOld implements Cloneable {

        public Map<MerchantRecipe, Double> fixedTrades;

        public ArrayList<ItemStack> currencies;

        public HashMap<Set<ItemStack>, Double> goods;

        public int maxRandomTrades;

        public double secondBuyChance;


        public AnimalTradesOld() {
            fixedTrades = new HashMap<>();
            currencies = new ArrayList<>();
            goods = new HashMap<>();
            maxRandomTrades = 0;
            secondBuyChance = 0.5;
        }

        private boolean tradeMatchesInputs(MerchantRecipe trade, ItemStack buy1, ItemStack buy2) {
            return Utils.doItemStacksMatch(trade.getItemToBuy(), buy1) && Utils.doItemStacksMatch(trade.getSecondItemToBuy(), buy2) ||
                    Utils.doItemStacksMatch(trade.getItemToBuy(), buy2) && Utils.doItemStacksMatch(trade.getSecondItemToBuy(), buy1);
        }

        private double trimChance(double chance) {
            return Math.min(Math.max(0.0, chance), 1.0);
        }

        /** Removes a trade if all the elements of the trade are equal to the parameter **/
        public void addTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, double chance) {
            this.fixedTrades.put(new MerchantRecipe(buy1, buy2, sell), trimChance(chance));
        }

        /** Removes a trade if all the elements of the trade are equal to the parameter **/
        public void addTrade(ItemStack buy1, ItemStack buy2, ItemStack sell) {
            this.fixedTrades.put(new MerchantRecipe(buy1, buy2, sell), 1.0);
        }

        /** Removes a trade if all the elements of the trade are equal to the parameter **/
        public void addTrade(ItemStack buy1, ItemStack sell, double chance) {
            this.fixedTrades.put(new MerchantRecipe(buy1, sell), trimChance(chance));
        }

        /** Removes a trade if all the elements of the trade are equal to the parameter **/
        public void addTrade(ItemStack buy1, ItemStack sell) {
            this.fixedTrades.put(new MerchantRecipe(buy1, sell), 1.0);
        }

        /** Removes a trade if all the elements of the trade are equal to the parameter **/
        public void removeTrade(ItemStack buy1, ItemStack buy2, ItemStack sell) {
            this.fixedTrades.keySet().removeIf(
                trade -> tradeMatchesInputs(trade, buy1, buy2) && trade.getItemToSell().getItem() == sell.getItem()
            );
        }

        /** Removes a trade if the sell item is the parameter **/
        public void removeTradeBySell(ItemStack sell) {
            this.fixedTrades.keySet().removeIf((
                    trade -> trade.getItemToSell().getItem() != sell.getItem()
            ));
        }

        /** Removes a trade if one of the two buy items is the parameter **/
        public void removeTradeByBuy(ItemStack buy) {
            this.fixedTrades.keySet().removeIf((
                    trade -> (trade.getItemToBuy().getItem() == buy.getItem()) ||
                             (trade.hasSecondItemToBuy() && trade.getSecondItemToBuy().getItem() == buy.getItem())
            ));
        }

        /** Removes a trade if first and second item to buy match the two parameters, in any order **/
        public void removeTradeByBuy(ItemStack buy1, ItemStack buy2) {
            this.fixedTrades.keySet().removeIf((
                    trade -> tradeMatchesInputs(trade, buy1, buy2)
            ));
        }

        /** Adds a currency (random item to buy) **/
        public void addCurrency(ItemStack stack) {
            this.currencies.add(stack);
        }

        /** Removes a currency (random item to buy) **/
        public void removeCurrency(ItemStack stack) {
            this.currencies.remove(stack);
        }

        /** Adds a good (random item to sell) **/
        public void addGoods(ItemStack... stacks) {
            addGoods(1.0, stacks);
        }

        /** Adds a good (random item to sell) **/
        public void addGoods(Double chance, ItemStack... stacks) {
            this.goods.put(new HashSet<>(Arrays.asList(stacks)), chance);
        }

        /** Removes a good from all sets that may contain it **/
        public void removeGood(ItemStack stack) {
            Iterator<Set<ItemStack>> it = this.goods.keySet().iterator();
            while (it.hasNext()) {
                Set<ItemStack> set = it.next();
                set.remove(stack);
                if (set.isEmpty()) it.remove();
            }
        }

        /** Returns the maximum number of random trades **/
        public int getMaxRandomTrades() {
            return maxRandomTrades;
        }

        /** Sets the maximum number of random trades **/
        public void setMaxRandomTrades(int randomTrades) {
            this.maxRandomTrades = randomTrades;
        }

        /** Returns the current chance of requiring a second item for the random trade **/
        public double getSecondBuyChance() {
            return secondBuyChance;
        }

        /** Sets the current chance of requiring a second item for the random trade **/
        public void setSecondBuyChance(double secondBuyChance) {
            this.secondBuyChance = secondBuyChance;
        }

        /** Returns all MerchantTrades without the chance **/
        public MerchantRecipeList getAllFixedTrades() {
            MerchantRecipeList list = new MerchantRecipeList();
            list.addAll(fixedTrades.keySet());
            return list;
        }

        /** Returns the current state of the fixed trades **/
        public Map<MerchantRecipe, Double> getFixedTrades() {
            return fixedTrades;
        }

        /** Returns the current state of the currencies **/
        public List<ItemStack> getCurrencies() {
            return currencies;
        }

        /** Returns the current state of the goods. Used by JEI, so the Map is flattened **/
        public Map<ItemStack, Double> getGoods() {
            Map<ItemStack, Double> map = new HashMap<>();
            for (Set<ItemStack> set : goods.keySet()) {
                double setSize = set.size();
                double setChance = goods.get(set);
                for (ItemStack stack : set) {
                    stack.setCount(1); // Ignore count
                    if (map.containsKey(stack)) {
                        double prevChance = map.get(stack);
                        map.put(stack, prevChance + setChance / setSize);
                    } else {
                        map.put(stack, setChance / setSize);
                    }
                }
            }
            return map;
        }

        /** Generates a list of trades based on the fixed trades and the other parameters **/
        public MerchantRecipeList generateTrades(Random rand) {
            MerchantRecipeList list = new MerchantRecipeList();
            for (Map.Entry<MerchantRecipe, Double> entry : fixedTrades.entrySet()) {
                if (rand.nextDouble() < entry.getValue()) {
                    list.add(entry.getKey());
                }
            }

            int count = 0;
            while (count < maxRandomTrades) {
                List<ItemStack> possibleGoods = generateFromMap(goods, rand);
                ItemStack buy1 = currencies.get(rand.nextInt(currencies.size()));
                ItemStack sell = possibleGoods.get(rand.nextInt(goods.size()));
                if (rand.nextDouble() < this.secondBuyChance) {
                    ItemStack buy2 = currencies.get(rand.nextInt(currencies.size()));
                    list.add(new MerchantRecipe(buy1, buy2, sell));
                } else {
                    list.add(new MerchantRecipe(buy1, sell));
                }
                count++;
            }

            return list;
        }

        private <T> List<T> generateFromMap(Map<Set<T>, Double> map, Random rand) {
            List<T> result = new ArrayList<>();
            for (Map.Entry<Set<T>, Double> entry : map.entrySet()) {
                if (rand.nextDouble() < entry.getValue()) {
                    // Transform set into list
                    ArrayList<T> list = new ArrayList<>(entry.getKey());
                    // Add a random element from the list, to the final set
                    result.add(list.get(rand.nextInt(list.size())));
                }
            }
            return result;
        }

        @Override
        public AnimalTradesOld clone() {
            AnimalTradesOld trades;
            try {
                trades = (AnimalTradesOld) super.clone();
            } catch (Exception ex) {
                trades = new AnimalTradesOld();
            }
            trades.setMaxRandomTrades(this.getMaxRandomTrades());
            trades.setSecondBuyChance(this.getSecondBuyChance());
            trades.goods = new HashMap<>(this.goods);
            trades.currencies = new ArrayList<>(this.currencies);
            trades.fixedTrades = new HashMap<>(this.fixedTrades);
            return trades;
        }
    }

}
