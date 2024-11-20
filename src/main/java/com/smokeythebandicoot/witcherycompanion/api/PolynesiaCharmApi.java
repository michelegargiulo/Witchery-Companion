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
import java.util.stream.Collectors;

public class PolynesiaCharmApi {

    private static final Map<Class<? extends EntityLiving>, AnimalTrades> animalTrades;

    static {
        animalTrades = new HashMap<>();

        // Common items available for all trades
        AnimalTrades entityLiving = new AnimalTrades();
        // All Entity Livings have one trade with one of those items, so trade chance is 100% and
        // the item is selected randomly from this list
        entityLiving.addFallbackTrade(new ItemStack(WitcheryIngredientItems.MANDRAKE_ROOT, 3), 1);
        entityLiving.addFallbackTrade(new ItemStack(WitcheryIngredientItems.BELLADONNA, 3), 1);
        entityLiving.addFallbackTrade(new ItemStack(WitcheryIngredientItems.ARTICHOKE, 3), 1);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.SAPLING, 4, 0), 3);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.SAPLING, 4, 1), 3);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.SAPLING, 4, 2), 3);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.SAPLING, 4, 3), 3);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.REEDS, 2), 1);
        entityLiving.addFallbackTrade(new ItemStack(Blocks.CACTUS, 2), 1);
        entityLiving.addFallbackTrade(new ItemStack(Items.GOLD_NUGGET, 5), 3);
        entityLiving.addFallbackTrade(new ItemStack(Items.IRON_INGOT, 2), 1);
        entityLiving.addFallbackTrade(new ItemStack(Items.BONE, 4), 3);
        entityLiving.addFallbackTrade(new ItemStack(Items.FLINT, 5), 3);
        entityLiving.addFallbackTrade(new ItemStack(WitcheryIngredientItems.DOG_TONGUE, 2), 1);
        entityLiving.addFallbackTrade(new ItemStack(Items.POTATO, 5), 3);
        entityLiving.addFallbackTrade(new ItemStack(Items.POISONOUS_POTATO, 2), 1);
        entityLiving.addFallbackTrade(new ItemStack(Items.CARROT, 5), 3);
        entityLiving.addFallbackTrade(new ItemStack(Items.CLAY_BALL, 10), 3);
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
        AnimalTrades trades = null;
        while (current != null) {
            if (animalTrades.containsKey(current)) {
                if (trades == null)
                    trades = animalTrades.get(current);
                else
                    trades.merge(animalTrades.get(current));
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



    public static class AnimalTrades implements Cloneable {

        // Initial list of goods. One always gets selected from this list
        public ArrayList<AnimalTradeStackInfo> fallbackGoods;

        // Currencies for the trades
        public ArrayList<AnimalTradeStackInfo> currencies;

        // Goods are added with a chance
        public ArrayList<AnimalTradeStackInfo> goods;

        // Chance that each animal requires a second currency for the trade
        private double secondBuyChance;


        public AnimalTrades() {
            currencies = new ArrayList<>();
            fallbackGoods = new ArrayList<>();
            goods = new ArrayList<>();
            secondBuyChance = 0.5;
        }

        public void addFallbackTrade(ItemStack good, int range, boolean precious) {
            fallbackGoods.add(new AnimalTradeStackInfo(good, range, 1.0, precious));
        }

        public void addFallbackTrade(ItemStack good, int range) {
            addFallbackTrade(good, range, false);
        }

        public void removeFallbackTrade(ItemStack good) {
            fallbackGoods.removeIf(trade -> trade.stack.getItem() == good.getItem() &&
                    trade.stack.getMetadata() == good.getMetadata());
        }

        public void addCurrency(ItemStack currency, int range) {
            currencies.add(new AnimalTradeStackInfo(currency, range, 1.0, false));
        }

        public void removeCurrency(ItemStack currency) {
            currencies.removeIf(trade -> trade.stack.getItem() == currency.getItem() &&
                    trade.stack.getMetadata() == currency.getMetadata());
        }

        public void addGood(ItemStack good, int range, double chance, boolean precious) {
            fallbackGoods.add(new AnimalTradeStackInfo(good, range, chance, precious));
        }

        public void removeGood(ItemStack good) {
            fallbackGoods.removeIf(trade -> trade.stack.getItem() == good.getItem() &&
                    trade.stack.getMetadata() == good.getMetadata());
        }

        public List<ItemStack> getPossibleGoods() {
            List<ItemStack> stacks = new ArrayList<>();
            for (AnimalTradeStackInfo stackInfo : this.fallbackGoods) {
                stacks.add(stackInfo.stack);
            }
            for (AnimalTradeStackInfo stackInfo : this.goods) {
                stacks.add(stackInfo.stack);
            }
            return stacks;
        }

        public List<ItemStack> getPossibleCurrencies() {
            return currencies.stream()
                    .map(currency -> currency.stack)
                    .collect(Collectors.toList());
        }

        public void merge(AnimalTrades otherTrades) {
            // Merge fallback trades

        }

        private ArrayList<AnimalTradeStackInfo> mergeStackInfos(ArrayList<AnimalTradeStackInfo> starting, ArrayList<AnimalTradeStackInfo> additional) {
            for (AnimalTradeStackInfo other : additional) {

            }
        }

        public double getSecondBuyChance() {
            return secondBuyChance;
        }

        public void setSecondBuyChance(double secondBuyChance) {
            this.secondBuyChance = Math.min(Math.max(0.0, secondBuyChance), 1.0);
        }

        /** Generates the actual trades based on chances, constraints, etc **/
        public MerchantRecipeList generateTrades(EntityLiving animal) {
            /** Process starts with a list of items (FALLBACK items)
             * An item is randomly selected from the FALLBACK items and added to GOODS list
             * Treefyd seeds are added to the GOODS list with 3% chance
             *
             * For each animal:
             * - currencies are added to the CURRENCY list
             * - items are added to the GOODS list with certain probability
             */
            Random rand = animal.getEntityWorld().rand;
            MerchantRecipeList list = new MerchantRecipeList();
            MerchantRecipeList finalList = new MerchantRecipeList();
            List<ItemStack> items = new ArrayList<>();
            items.add(fallbackGoods.get(rand.nextInt(fallbackGoods.size())).stack);

            // This is the same Witchery code, but ported for the new info
            Iterator<AnimalTradeStackInfo> it = goods.iterator();
            while(true) {
                AnimalTradeStackInfo trade;
                do {
                    if (!it.hasNext()) {
                        Collections.shuffle(list);
                        int MAX_ITEMS = rand.nextInt(2) + 1;

                        for(int i = 0; i < MAX_ITEMS && i < list.size(); ++i) {
                            finalList.add(list.get(i));
                        }

                        return finalList;
                    }

                    trade = it.next();
                } while(trade.stack.isEmpty());

                // Copy stack and set count
                ItemStack good = trade.stack.copy();
                good.setCount(Math.min(rand.nextInt(trade.stack.getCount()) + trade.range, good.getMaxStackSize()));

                ItemStack currency = currencies.get(rand.nextInt(currencies.size())).stack;
                ItemStack cost = currency.copy();
                int multiplier = 1;
                if (trade.precious || animal.isEntityUndead()) {
                    multiplier = 2;
                }

                int factor = good.getCount() > 4 ? 1 : 2;
                cost.setCount(Math.min(rand.nextInt(2) + good.getCount() * multiplier * (rand.nextInt(2) + factor), currency.getMaxStackSize()));
                MerchantRecipe recipe = new MerchantRecipe(cost, good);
                recipe.increaseMaxTradeUses(-(6 - rand.nextInt(2)));
                list.add(recipe);
            }
        }

        private static class AnimalTradeStackInfo implements Cloneable {

            private ItemStack stack;        // The base stack
            private int range = 0;              // Min count will be stack.count, max count will be stack.count + range
            private double chance = 1.0;        // Chance that the trade will be selected
            private boolean precious = false;   // If true, increases the multiplier to 2 for the currency requirement

            public AnimalTradeStackInfo(ItemStack stack, int range, double chance, boolean precious) {
                this.stack = stack;
                this.range = range;
                this.chance = chance;
                this.precious = precious;
            }

            @Override
            protected AnimalTradeStackInfo clone() {
                return new AnimalTradeStackInfo(this.stack.copy(), this.range, this.chance, this.precious);
            }
        }

        @Override
        public AnimalTrades clone() {
            AnimalTrades trades;
            try {
                trades = (AnimalTrades) super.clone();
            } catch (Exception ex) {
                trades = new AnimalTrades();
            }
            trades.setSecondBuyChance(this.getSecondBuyChance());
            trades.goods = cloneAnimalTradeInfos(this.goods);
            trades.currencies = cloneAnimalTradeInfos(this.currencies);
            trades.fallbackGoods = cloneAnimalTradeInfos(this.fallbackGoods);
            return trades;
        }

        private ArrayList<AnimalTradeStackInfo> cloneAnimalTradeInfos(ArrayList<AnimalTradeStackInfo> stackInfos) {
            ArrayList<AnimalTradeStackInfo> result = new ArrayList<>();
            result.addAll(stackInfos.stream()
                    .map(item -> item.clone())
                    .collect(Collectors.toList()));
            return result;
        }

    }


}
