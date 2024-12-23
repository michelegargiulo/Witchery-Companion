package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import java.util.*;
import java.util.stream.Collectors;

public class PolynesiaCharmApi {

    private static boolean climbHierarchy = true;
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

        // Every living entity has a 3% chance to have an additional trade with Treefyd Seeds
        entityLiving.addGood(new ItemStack(WitcheryIngredientItems.TREEFYD_SEEDS), 0, 0.03, true);
        animalTrades.put(EntityLiving.class, entityLiving);

        // All Mobs
        AnimalTrades mobs = new AnimalTrades();
        mobs.addCurrency(new ItemStack(Items.BONE), 3);
        animalTrades.put(EntityZombie.class, mobs);

        // All Animals
        AnimalTrades animals = new AnimalTrades();
        animals.addCurrency((new ItemStack(Items.BEEF)), 3);
        animals.addCurrency((new ItemStack(Items.PORKCHOP)), 3);
        animals.addCurrency((new ItemStack(Items.CHICKEN)), 3);
        animals.addCurrency((new ItemStack(Items.FISH)), 3);
        animals.addCurrency((new ItemStack(Items.WHEAT)), 3);
        animals.addCurrency((new ItemStack(Items.WHEAT_SEEDS)), 3);
        animals.addCurrency((new ItemStack(Items.CARROT)), 3);
        animals.addCurrency((new ItemStack(Items.APPLE)), 3);
        animals.addCurrency((new ItemStack(Items.POTATO)), 3);
        animalTrades.put(EntityAnimal.class, animals);

        // PIG
        AnimalTrades pig = new AnimalTrades();
        pig.addCurrency(new ItemStack(Items.CARROT), 3);
        pig.addCurrency(new ItemStack(Items.APPLE), 3);
        pig.addCurrency(new ItemStack(Items.POTATO), 3);
        pig.addGood(new ItemStack(Blocks.RED_MUSHROOM, 5), 4);
        pig.addGood(new ItemStack(Blocks.BROWN_MUSHROOM, 5), 4);
        pig.addGood(new ItemStack(Items.EMERALD, 1), 1, 0.02, true);
        pig.addGood(new ItemStack(Items.DIAMOND, 1), 1, 0.01, true);
        animalTrades.put(EntityPig.class, pig);

        // Horse
        AnimalTrades horse = new AnimalTrades();
        horse.addCurrency(new ItemStack(Items.CARROT), 3);
        horse.addCurrency(new ItemStack(Items.APPLE), 3);
        horse.addCurrency(new ItemStack(Items.WHEAT), 3);
        horse.addGood(new ItemStack(Items.SADDLE), 1, 0.01, true);
        animalTrades.put(EntityHorse.class, horse);

        // Wolf
        AnimalTrades wolf = new AnimalTrades();
        wolf.addCurrency(new ItemStack(Items.BEEF), 3);
        wolf.addCurrency(new ItemStack(Items.PORKCHOP), 3);
        wolf.addCurrency(new ItemStack(Items.CHICKEN), 3);
        wolf.addGood(new ItemStack(Items.BONE, 5), 4);
        wolf.addGood(new ItemStack(Items.EMERALD, 1), 1, 0.02, true);
        wolf.addGood(new ItemStack(Items.DIAMOND, 1), 1, 0.01, true);
        animalTrades.put(EntityWolf.class, wolf);

        // Ocelot
        AnimalTrades ocelot = new AnimalTrades();
        ocelot.addCurrency(new ItemStack(Items.MILK_BUCKET), 1);
        ocelot.addCurrency(new ItemStack(Items.FISH), 3);
        animalTrades.put(EntityOcelot.class, ocelot);

        // Mooshroom
        AnimalTrades mooshroom = new AnimalTrades();
        mooshroom.addCurrency(new ItemStack(Blocks.RED_MUSHROOM), 3);
        mooshroom.addCurrency(new ItemStack(Blocks.BROWN_MUSHROOM), 3);
        animalTrades.put(EntityMooshroom.class, mooshroom);

        // Cow
        AnimalTrades cow = new AnimalTrades();
        cow.addCurrency(new ItemStack(Items.WHEAT), 3);
        animalTrades.put(EntityCow.class, cow);

        // Chicken
        AnimalTrades chicken = new AnimalTrades();
        chicken.addCurrency(new ItemStack(Items.WHEAT_SEEDS), 3);
        chicken.addGood(new ItemStack(Items.FEATHER, 10), 5);
        chicken.addGood(new ItemStack(Items.EGG, 5), 5);
        animalTrades.put(EntityChicken.class, chicken);

        // Sheep
        AnimalTrades sheep = new AnimalTrades();
        sheep.addCurrency(new ItemStack(Items.WHEAT), 3);
        animalTrades.put(EntitySheep.class, sheep);

        // Squid
        AnimalTrades squid = new AnimalTrades();
        squid.addCurrency(new ItemStack(Items.FISH), 3);
        squid.addGood(new ItemStack(Items.DYE, 10, EnumDyeColor.BLACK.getDyeDamage()), 5);
        animalTrades.put(EntitySquid.class, squid);

        // Bat
        AnimalTrades bat = new AnimalTrades();
        bat.addCurrency(new ItemStack(Items.WHEAT_SEEDS), 3);
        bat.addCurrency(new ItemStack(Items.WHEAT), 3);
        bat.addCurrency(new ItemStack(Items.BEEF), 3);
        bat.addCurrency(new ItemStack(Items.PORKCHOP), 3);
        bat.addGood(new ItemStack(WitcheryIngredientItems.BAT_WOOL, 5), 3);
        animalTrades.put(EntityBat.class, bat);

        // Spider
        AnimalTrades spider = new AnimalTrades();
        spider.addCurrency(new ItemStack(Items.BEEF), 3);
        spider.addCurrency(new ItemStack(Items.PORKCHOP), 3);
        spider.addCurrency(new ItemStack(Items.CHICKEN), 3);
        spider.addCurrency(new ItemStack(Items.FISH), 3);
        spider.addGood(new ItemStack(Items.STRING, 8), 5);
        spider.addGood(new ItemStack(WitcheryIngredientItems.WEB, 4), 3);
        animalTrades.put(EntitySpider.class, spider);

        // Creeper
        AnimalTrades creeper = new AnimalTrades();
        creeper.addCurrency(new ItemStack(Items.GUNPOWDER), 3);
        creeper.addCurrency(new ItemStack(Items.FISH), 3);
        creeper.addGood(new ItemStack(WitcheryIngredientItems.SPECTRAL_DUST, 2), 1, 0.05);
        creeper.addGood(new ItemStack(WitcheryIngredientItems.TREEFYD_SEEDS), 1, 0.1);
        creeper.addGood(new ItemStack(WitcheryIngredientItems.CREEPER_HEART), 1, 0.1);
        animalTrades.put(EntityCreeper.class, creeper);

        // Zombies, Skeletons
        AnimalTrades undead = new AnimalTrades();
        undead.addGood(new ItemStack(WitcheryIngredientItems.SPECTRAL_DUST, 2), 1, 0.05);
        animalTrades.put(EntityZombie.class, undead);
        animalTrades.put(AbstractSkeleton.class, undead);
        animalTrades.put(EntityWitherSkeleton.class, undead);
        animalTrades.put(EntityZombieHorse.class, undead);
        animalTrades.put(EntitySkeletonHorse.class, undead);

    }


    private static AnimalTrades getData(EntityLiving entity) {
        Class<?> current = entity.getClass();
        AnimalTrades trades = null;
        if (climbHierarchy) {
            while (current != null) {
                if (animalTrades.containsKey(current)) {
                    if (trades == null)
                        trades = animalTrades.get(current);
                    else
                        trades.merge(animalTrades.get(current));
                }
                current = current.getSuperclass();
            }
        } else {
            return animalTrades.getOrDefault(current, null);
        }
        return trades;
    }


    /** If true, the API will combine the trades for the animal type, going up in the hierarchy.
     * For example, if there are trades for EntityChicken, EntityAnimal and EntityLiving, a chicken
     * would have all the trades, while a Pig would only have the ones from EntityAnimal and EntityLiving.
     * If false, every animal must be explicitly present in the hierarchy and have its own trades **/
    public static void setClimbHierarchy(boolean climbHierarchy) {
        climbHierarchy = climbHierarchy;
    }

    /** Adds a fallback trade to the class. Good is the item to sell, range specifies a maximum variation in the amount from the
     * good ItemStack's count. If isPrecious is true, the good will require a second item to buy to be sold **/
    public static void addFallbackTrade(Class<? extends EntityLiving> entityClass, ItemStack good, int range, boolean isPrecious) {
        animalTrades.putIfAbsent(entityClass, new AnimalTrades());
        animalTrades.get(entityClass).addFallbackTrade(
            good, range, isPrecious
        );
    }

    public static void addFallbackTrade(Class<? extends EntityLiving> entityClass, ItemStack good, int range) {
        addFallbackTrade(entityClass, good, range, false);
    }

    public static void addFallbackTrade(Class<? extends EntityLiving> entityClass, ItemStack good) {
        addFallbackTrade(entityClass, good, 1, false);
    }

    /** Removes a fallback trade for an animal class if there's an animal-specific trade corresponding to the good.
     * The item might still be present in the hierarchy **/
    public static void removeFallbackTrade(Class<? extends EntityLiving> entityClass, ItemStack good) {
        if (animalTrades.containsKey(entityClass)) {
            animalTrades.get(entityClass).removeFallbackTrade(good);
        }
    }

    /** Adds a good that can be sold. Range indicates the variation in amount with respect to the good ItemStack's count, chance
     * is the chance to appear as a trade, and if isPrecious is true the good requires a second item to buy for it to be sold **/
    public static void addGood(Class<? extends EntityLiving> entityClass, ItemStack good, int range, double chance, boolean isPrecious) {
        animalTrades.putIfAbsent(entityClass, new AnimalTrades());
        animalTrades.get(entityClass).addGood(good, range, chance, isPrecious);
    }

    public static void addGood(Class<? extends EntityLiving> entityClass, ItemStack good, int range, double chance) {
        addGood(entityClass, good, range, chance, false);
    }

    public static void addGood(Class<? extends EntityLiving> entityClass, ItemStack good, int range) {
        addGood(entityClass, good, range, 1.0, false);
    }

    public static void addGood(Class<? extends EntityLiving> entityClass, ItemStack good) {
        addGood(entityClass, good, 0, 1.0, false);
    }

    /** Removes a good for an animal class if there's an animal-specific trade corresponding to the good.
     * The item might still be present in the hierarchy  **/
    public static void removeGood(Class<? extends EntityLiving> entityClass, ItemStack good) {
        if (animalTrades.containsKey(entityClass)) {
            animalTrades.get(entityClass).removeGood(good);
        }
    }

    /** Adds a currency, that represents an item that the animal is interested to buy. Range indicates the variation in amount
     * with respect to the good ItemStack's count  **/
    public static void addCurrency(Class<? extends EntityLiving> entityClass, ItemStack currency, int range) {
        animalTrades.putIfAbsent(entityClass, new AnimalTrades());
        animalTrades.get(entityClass).addCurrency(currency, range);
    }

    public static void addCurrency(Class<? extends EntityLiving> entityClass, ItemStack currency) {
        addCurrency(entityClass, currency, 1);
    }

    /** Removes a currency for an animal class if there's an animal-specific trade corresponding to the currency.
     * The item might still be present in the hierarchy  **/
    public static void removeCurrency(Class<? extends EntityLiving> entityClass, ItemStack currency) {
        animalTrades.putIfAbsent(entityClass, new AnimalTrades());
        animalTrades.get(entityClass).removeCurrency(currency);
    }

    /** Actually generates trades for a living entity based on its class and world (for randomness) **/
    public static MerchantRecipeList generateTradesFor(EntityLiving entity) {
        AnimalTrades trades = getData(entity);
        if (trades != null) {
            return trades.generateTrades(entity);
        }
        return new MerchantRecipeList();
    }

    /** Removes all trades, goods and currencies for an EntityLiving **/
    public static void removeAllForAnimal(Class<? extends EntityLiving> entityClass) {
        animalTrades.remove(entityClass);
    }

    /** Removes everything from the API, including the built-in trades **/
    public static void clearAll() {
        animalTrades.clear();
    }



    private static class AnimalTrades {

        // Initial list of goods. One always gets selected from this list
        private ArrayList<AnimalTradeStackInfo> fallbackGoods;

        // Currencies for the trades
        private ArrayList<AnimalTradeStackInfo> currencies;

        // Goods are added with a chance
        private ArrayList<AnimalTradeStackInfo> goods;

        // Chance that each animal requires a second currency for the trade
        private double secondBuyChance;


        private AnimalTrades() {
            currencies = new ArrayList<>();
            fallbackGoods = new ArrayList<>();
            goods = new ArrayList<>();
            secondBuyChance = 0.5;
        }

        private void addFallbackTrade(ItemStack good, int range, boolean precious) {
            fallbackGoods.add(new AnimalTradeStackInfo(good, range, 1.0, precious));
        }

        private void addFallbackTrade(ItemStack good, int range) {
            addFallbackTrade(good, range, false);
        }

        private void removeFallbackTrade(ItemStack good) {
            fallbackGoods.removeIf(trade -> trade.stack.getItem() == good.getItem() &&
                    trade.stack.getMetadata() == good.getMetadata());
        }

        private void addCurrency(ItemStack currency, int range) {
            currencies.add(new AnimalTradeStackInfo(currency, range, 1.0, false));
        }

        private void removeCurrency(ItemStack currency) {
            currencies.removeIf(trade -> trade.stack.getItem() == currency.getItem() &&
                    trade.stack.getMetadata() == currency.getMetadata());
        }

        private void addGood(ItemStack good, int range, double chance, boolean precious) {
            goods.add(new AnimalTradeStackInfo(good, range, chance, precious));
        }

        private void addGood(ItemStack good, int range, double chance) {
            addGood(good, range, chance, false);
        }

        private void addGood(ItemStack good, int range) {
            addGood(good, range, 1.0, false);
        }

        private void removeGood(ItemStack good) {
            goods.removeIf(trade -> trade.stack.getItem() == good.getItem() &&
                    trade.stack.getMetadata() == good.getMetadata());
        }

        private List<ItemStack> getPossibleGoods() {
            List<ItemStack> stacks = new ArrayList<>();
            for (AnimalTradeStackInfo stackInfo : this.fallbackGoods) {
                stacks.add(stackInfo.stack);
            }
            for (AnimalTradeStackInfo stackInfo : this.goods) {
                stacks.add(stackInfo.stack);
            }
            return stacks;
        }

        private List<ItemStack> getPossibleCurrencies() {
            return currencies.stream()
                    .map(currency -> currency.stack)
                    .collect(Collectors.toList());
        }

        /** Merge allows for duplicate entries! **/
        private void merge(AnimalTrades otherTrades) {
            this.fallbackGoods.addAll(otherTrades.fallbackGoods);
            this.goods.addAll(otherTrades.goods);
            this.currencies.addAll(otherTrades.currencies);
        }

        private double getSecondBuyChance() {
            return secondBuyChance;
        }

        private void setSecondBuyChance(double secondBuyChance) {
            this.secondBuyChance = Math.min(Math.max(0.0, secondBuyChance), 1.0);
        }

        /** Generates the actual trades based on chances, constraints, etc **/
        private MerchantRecipeList generateTrades(EntityLiving animal) {
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
            List<AnimalTradeStackInfo> items = new ArrayList<>();
            items.add(fallbackGoods.get(rand.nextInt(fallbackGoods.size())));
            items.addAll(goods);

            // This is the same Witchery code, but ported for the new info
            Iterator<AnimalTradeStackInfo> it = items.iterator();
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
                good.setCount(Math.min(rand.nextInt(trade.stack.getCount()) + trade.range + 1, good.getMaxStackSize()));

                ItemStack currency = currencies.get(rand.nextInt(currencies.size())).stack;
                ItemStack cost = currency.copy();
                int multiplier = 1;
                if (trade.precious || animal.isEntityUndead()) {
                    multiplier = 2;
                }

                int factor = good.getCount() > 4 ? 1 : 2;
                cost.setCount(Math.min(rand.nextInt(2) + 1 + good.getCount() * multiplier * (rand.nextInt(2) + factor), currency.getMaxStackSize()));
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

    }


}
