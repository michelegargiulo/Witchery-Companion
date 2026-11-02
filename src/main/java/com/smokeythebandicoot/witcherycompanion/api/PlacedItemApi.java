package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.utils.ItemMetaMap;
import com.smokeythebandicoot.witcherycompanion.utils.ItemMetaSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockPlacedItem;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Controls which items can be placed on top of the Altar (they become a "Placed Item" block, like Witchery does with
 * the Arthana).
 * <p>
 * Witchery hardcodes this inside three items (Arthana, Mystic Branch and Kobolite Pentacle): each one checks by itself
 * that the player is right-clicking the top face of an Altar with air above it, and places itself. This API replaces
 * that logic with a list:
 * <ul>
 *     <li>Default list: Arthana, Mystic Branch, Kobolite Pentacle, so nothing changes if the list is never touched</li>
 *     <li>Those three items are the "native" ones: while they are in the list Witchery's own code places them
 *     (exactly as before); when they are removed, a mixin on each of them stops Witchery's code from placing them</li>
 *     <li>Any other item in the list is placed by a hook on {@code ItemStack.onItemUse}
 *     ({@link #tryPlaceOnAltar})</li>
 * </ul>
 * It also decides what a placed item does for the Altar (see {@link PlacedItemBooster}): each item can be a booster that
 * changes the four Altar parameters (recharge rate, max power, range, enhancement level). Witchery's defaults are the
 * Arthana (range +1), the Mystic Branch (enhancement +1) and the Kobolite Pentacle (recharge x2); each booster counts
 * only once per Altar, however many items trigger it.
 * <p>
 * Matching is done on the item and, only for items that really use the metadata as a subtype, on the metadata (NBT is
 * ignored). Items that do not have subtypes (and damageable items, whose metadata is their damage) match whatever
 * their metadata is. A wildcard metadata ({@link ItemMetaSet#WILDCARD}) matches every metadata.
 */
public class PlacedItemApi {

    private static final ItemMetaSet<Item> placeableItems = new ItemMetaSet<>();

    /** Items whose placement is hardcoded in Witchery's own item classes */
    private static final Set<Item> nativeItems = new HashSet<>();

    /** What the placed items do for the Altar. Can be changed */
    private static final ItemMetaMap<Item, PlacedItemBooster> boosters = new ItemMetaMap<>();

    /** What the placed items do for the Altar in Witchery. Never changed, used when the integration is disabled */
    private static final ItemMetaMap<Item, PlacedItemBooster> defaultBoosters = new ItemMetaMap<>();

    static {
        initDefaults();
    }

    /** Fills the list with the items that Witchery places by default. Called once, at class initialization */
    private static void initDefaults() {
        nativeItems.add(WitcheryGeneralItems.getArthana());
        nativeItems.add(WitcheryGeneralItems.MYSTIC_BRANCH);
        nativeItems.add(WitcheryIngredientItems.KOBOLDITE_PENTACLE);
        nativeItems.remove(null);

        for (Item item : nativeItems) {
            placeableItems.add(item, ItemMetaSet.WILDCARD);
        }

        // Witchery's boosts: Arthana increases range, Mystic Branch enhancement, Kobolite Pentacle multiplies recharge rate
        initDefaultBooster(WitcheryGeneralItems.getArthana(), PlacedItemBooster.of(0, 0, 1, 0));
        initDefaultBooster(WitcheryGeneralItems.MYSTIC_BRANCH, PlacedItemBooster.of(0, 0, 0, 1));
        initDefaultBooster(WitcheryIngredientItems.KOBOLDITE_PENTACLE, PlacedItemBooster.of(0, 0, 0, 0, 2, 1, 1, 1));
    }

    private static void initDefaultBooster(Item item, PlacedItemBooster booster) {
        if (item != null) {
            boosters.put(item, ItemMetaSet.WILDCARD, booster);
            defaultBoosters.put(item, ItemMetaSet.WILDCARD, booster);
        }
    }


    /// REGISTRATION

    /** The stack can be placed on the Altar. Returns false if it already could (or if the stack is empty) */
    public static boolean addPlacedItem(ItemStack stack) {
        return !stack.isEmpty() && placeableItems.add(stack.getItem(), effectiveMeta(stack));
    }

    /** The stack can not be placed on the Altar anymore. Returns false if it could not be placed in the first place */
    public static boolean removePlacedItem(ItemStack stack) {
        return !stack.isEmpty() && placeableItems.remove(stack.getItem(), effectiveMeta(stack));
    }

    /** Removes every item from the list, including the defaults: nothing can be placed on the Altar anymore */
    public static void clearPlacedItems() {
        placeableItems.clear();
    }


    /// QUERIES

    /** Returns true if the stack can be placed on top of the Altar */
    public static boolean canPlace(ItemStack stack) {
        return !stack.isEmpty() && placeableItems.contains(stack.getItem(), effectiveMeta(stack));
    }

    /** Returns true if the item is one of those whose placement is hardcoded in Witchery's own item classes */
    public static boolean isNativeItem(Item item) {
        return nativeItems.contains(item);
    }

    /// BOOSTERS

    /**
     * What a placed item does for the Altar: changes some of the four Altar parameters. A booster counts only once per
     * Altar, even if many placed items trigger it, so a booster object should not be shared between unrelated items
     */
    public static class PlacedItemBooster {

        private final BiConsumer<ItemStack, AltarApi.AltarBoosterInfo> consumer;

        /** Custom boost, for other mods. The consumer changes the info, see {@link AltarApi.AltarBoosterInfo} */
        public PlacedItemBooster(BiConsumer<ItemStack, AltarApi.AltarBoosterInfo> consumer) {
            this.consumer = consumer;
        }

        /** Booster that only adds increments to recharge rate, max power, range and enhancement level */
        public static PlacedItemBooster of(int rechargeInc, int powerInc, int rangeInc, int enhancementInc) {
            return of(rechargeInc, powerInc, rangeInc, enhancementInc, 1, 1, 1, 1);
        }

        /** Booster that adds increments and multiplies. Multipliers are applied after all increments of all boosters */
        public static PlacedItemBooster of(int rechargeInc, int powerInc, int rangeInc, int enhancementInc,
                                           int rechargeMul, int powerMul, int rangeMul, int enhancementMul) {
            return new PlacedItemBooster((stack, info) -> {
                info.add(rechargeInc, powerInc, rangeInc, enhancementInc);
                info.multiply(rechargeMul, powerMul, rangeMul, enhancementMul);
            });
        }

        public void apply(ItemStack stack, AltarApi.AltarBoosterInfo info) {
            consumer.accept(stack, info);
        }
    }

    /**
     * Makes the stack a booster. It also makes it placeable on the Altar (it would be useless otherwise), but removing
     * it from the placeable items later does not remove the booster: items already placed keep working
     */
    public static void registerBooster(ItemStack stack, PlacedItemBooster booster) {
        if (stack.isEmpty() || booster == null) {
            return;
        }
        addPlacedItem(stack);
        boosters.put(stack.getItem(), effectiveMeta(stack), booster);
    }

    /** The stack is not a booster anymore (it stays placeable). Returns false if it was not a booster */
    public static boolean unregisterBooster(ItemStack stack) {
        return !stack.isEmpty() && boosters.remove(stack.getItem(), effectiveMeta(stack));
    }

    /** Removes every booster, including the defaults: no placed item has an effect anymore */
    public static void clearBoosters() {
        boosters.clear();
    }

    /** Returns the booster of the stack, or null if it is not a booster */
    public static PlacedItemBooster getBooster(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : boosters.get(stack.getItem(), effectiveMeta(stack));
    }

    /** Returns what Witchery itself does for the stack (ignores any change), or null if it does nothing */
    public static PlacedItemBooster getDefaultBooster(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : defaultBoosters.get(stack.getItem(), effectiveMeta(stack));
    }


    /** Metadata that is actually used to match the stack: the wildcard if the item does not use it as a subtype */
    private static int effectiveMeta(ItemStack stack) {
        Item item = stack.getItem();
        return ItemMetaSet.effectiveMeta(item.getHasSubtypes() && !item.isDamageable(), stack.getMetadata());
    }


    /// PLACEMENT (used by mixins)

    /**
     * Witchery's own placement condition: the player right-clicks the top face of an Altar and the block above is air.
     * Has no side effects
     */
    public static boolean isAltarTop(World world, BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.UP &&
                world.getBlockState(pos).getBlock() == WitcheryBlocks.ALTAR &&
                world.isAirBlock(pos.up());
    }

    /**
     * Used by the mixins on the three native items, at the head of their {@code onItemUse}. Returns true if Witchery's
     * hardcoded placement has to be skipped: the player is aiming at the top of an Altar, so Witchery would place the
     * item, but the item is not in the list anymore
     */
    public static boolean shouldBlockNativePlacement(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
        return isAltarTop(world, pos, facing) && !canPlace(player.getHeldItem(hand));
    }

    /**
     * Used by the hook on {@code ItemStack.onItemUse}: places the held item on the Altar if it is in the list and is
     * not one of the native items (those place themselves). Returns null if the use was not handled, so that the
     * normal item logic carries on.
     * <p>
     * Unlike Witchery's native items, which return PASS on the client, this returns SUCCESS on both sides: this makes
     * the client stop the right click here instead of also "using" the item (eating the food that was just placed...)
     */
    public static EnumActionResult tryPlaceOnAltar(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing) {
        if (facing != EnumFacing.UP || stack.isEmpty() || isNativeItem(stack.getItem())) {
            return null;
        }
        // Witchery places "the item the player holds in that hand": make sure it is this very stack
        if (player.getHeldItem(hand) != stack) {
            return null;
        }
        if (!isAltarTop(world, pos, facing) || !canPlace(stack)) {
            return null;
        }

        Item item = stack.getItem();
        BlockPlacedItem.placeInWorld(hand, player, world, pos.up());
        player.addStat(StatList.getObjectUseStats(item));
        return EnumActionResult.SUCCESS;
    }
}
