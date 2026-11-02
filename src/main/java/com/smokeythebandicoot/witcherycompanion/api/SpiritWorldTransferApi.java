package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.utils.ItemMetaSet;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryBrewItems;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Controls which items cross the barrier of the Spirit World together with the player.
 * <p>
 * Normally, when the player enters the Spirit World the whole inventory is stored and replaced by the one the player
 * had the last time they were there. Witchery makes some items the exception: those are taken out of the inventory
 * before the swap and given back right after it. This API holds the two lists of exceptions:
 * <ul>
 *     <li>Entering: items carried from the Overworld into the Spirit World (default: Icy Needle, Mutandis)</li>
 *     <li>Exiting: items carried from the Spirit World back to the Overworld (default: Wispy Cotton, Disturbed
 *     Cotton, Mellifluous Hunger, Flowing Spirit, Subdued Spirit, Icy Needle)</li>
 * </ul>
 * An item in both lists goes back and forth (like the Icy Needle). Matching is done by item and metadata only (NBT is
 * ignored). Items registered with {@link ItemMetaSet#WILDCARD} metadata match every metadata
 */
public class SpiritWorldTransferApi {

    private static final ItemMetaSet<Item> enteringItems = new ItemMetaSet<>();
    private static final ItemMetaSet<Item> exitingItems = new ItemMetaSet<>();

    /** Items that Witchery already transfers by itself, with its hardcoded logic. */
    private static final Set<Item> nativeEntering = new HashSet<>();
    private static final Set<Item> nativeExiting = new HashSet<>();

    /** Items taken out of the inventory by the mixins and not yet given back, by player. Server thread only */
    private static final Map<UUID, List<ItemStack>> pendingEntering = new HashMap<>();
    private static final Map<UUID, List<ItemStack>> pendingExiting = new HashMap<>();

    static {
        initDefaults();
    }

    /** Fills the lists with the items that Witchery transfers by default. Called once, at class initialization */
    private static void initDefaults() {
        Item wispyCotton = Item.getItemFromBlock(WitcheryBlocks.WISPY_COTTON);

        // Overworld -> Spirit World
        nativeEntering.add(WitcheryIngredientItems.ICY_NEEDLE);
        nativeEntering.add(WitcheryIngredientItems.MUTANDIS);

        // Spirit World -> Overworld
        nativeExiting.add(wispyCotton);
        nativeExiting.add(WitcheryIngredientItems.DISTURBED_COTTON);
        nativeExiting.add(WitcheryFumeItems.MELLIFLUOUS_HUNGER);
        nativeExiting.add(WitcheryBrewItems.FLOWING_SPIRIT_BREW);
        nativeExiting.add(WitcheryIngredientItems.SUBDUED_SPIRIT);
        nativeExiting.add(WitcheryIngredientItems.ICY_NEEDLE);

        for (Item item : nativeEntering) {
            enteringItems.add(item, 0);
        }
        for (Item item : nativeExiting) {
            exitingItems.add(item, 0);
        }
    }


    /// REGISTRATION

    /** The stack will be carried from the Overworld into the Spirit World. Returns false if already registered */
    public static boolean addEnteringItem(ItemStack stack) {
        return !stack.isEmpty() && enteringItems.add(stack.getItem(), stack.getMetadata());
    }

    /** The stack will not be carried into the Spirit World anymore. Returns false if it was not registered */
    public static boolean removeEnteringItem(ItemStack stack) {
        return !stack.isEmpty() && enteringItems.remove(stack.getItem(), stack.getMetadata());
    }

    /** The stack will be carried from the Spirit World to the Overworld. Returns false if already registered */
    public static boolean addExitingItem(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.add(stack.getItem(), stack.getMetadata());
    }

    /** The stack will not be carried out of the Spirit World anymore. Returns false if it was not registered */
    public static boolean removeExitingItem(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.remove(stack.getItem(), stack.getMetadata());
    }

    /** Removes every item from the entering list, including the defaults */
    public static void clearEnteringItems() {
        enteringItems.clear();
    }

    /** Removes every item from the exiting list, including the defaults */
    public static void clearExitingItems() {
        exitingItems.clear();
    }


    /// QUERIES

    /** Returns true if the stack is carried from the Overworld into the Spirit World */
    public static boolean canEnter(ItemStack stack) {
        return !stack.isEmpty() && enteringItems.contains(stack.getItem(), stack.getMetadata());
    }

    /** Returns true if the stack is carried from the Spirit World to the Overworld */
    public static boolean canExit(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.contains(stack.getItem(), stack.getMetadata());
    }

    /** Same as {@link #canEnter(ItemStack)}, for an item and a metadata */
    public static boolean canEnter(Item item, int meta) {
        return item != null && enteringItems.contains(item, meta);
    }

    /** Same as {@link #canExit(ItemStack)}, for an item and a metadata */
    public static boolean canExit(Item item, int meta) {
        return item != null && exitingItems.contains(item, meta);
    }


    /// TRANSFER (used by mixins)

    /**
     * Takes out of the player's inventory (slots and cursor) the items that have to be carried into the Spirit World
     * and that Witchery does not carry by itself. They are kept until {@link #restoreEnteringItems} is called
     */
    public static void extractEnteringItems(EntityPlayer player) {
        pendingEntering.put(player.getUniqueID(), extract(player.inventory, enteringItems, nativeEntering));
    }

    /** Gives back to the player the items taken out by {@link #extractEnteringItems}. Does nothing if none */
    public static void restoreEnteringItems(EntityPlayer player) {
        restore(player, pendingEntering.remove(player.getUniqueID()));
    }

    /**
     * Takes out of the player's inventory (slots and cursor) the items that have to be carried out of the Spirit World
     * and that Witchery does not carry by itself. They are kept until {@link #restoreExitingItems} is called
     */
    public static void extractExitingItems(EntityPlayer player) {
        pendingExiting.put(player.getUniqueID(), extract(player.inventory, exitingItems, nativeExiting));
    }

    /** Gives back to the player the items taken out by {@link #extractExitingItems}. Does nothing if none */
    public static void restoreExitingItems(EntityPlayer player) {
        restore(player, pendingExiting.remove(player.getUniqueID()));
    }

    private static List<ItemStack> extract(InventoryPlayer inventory, ItemMetaSet<Item> set, Set<Item> handledByWitchery) {
        if (set.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> extracted = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (shouldExtract(stack, set, handledByWitchery)) {
                extracted.add(stack.copy());
                inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
            }
        }

        // Also the stack that the player is holding with the cursor, like InventoryPlayer.clearMatchingItems does
        ItemStack cursor = inventory.getItemStack();
        if (shouldExtract(cursor, set, handledByWitchery)) {
            extracted.add(cursor.copy());
            inventory.setItemStack(ItemStack.EMPTY);
        }
        return extracted;
    }

    private static boolean shouldExtract(ItemStack stack, ItemMetaSet<Item> set, Set<Item> handledByWitchery) {
        return !stack.isEmpty() &&
                !handledByWitchery.contains(stack.getItem()) &&
                set.contains(stack.getItem(), stack.getMetadata());
    }

    private static void restore(EntityPlayer player, List<ItemStack> stacks) {
        if (stacks == null) {
            return;
        }
        for (ItemStack stack : stacks) {
            // Whatever does not fit in the inventory is dropped, so that nothing is lost
            player.inventory.addItemStackToInventory(stack);
            if (!stack.isEmpty()) {
                player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY + 0.5, player.posZ, stack));
            }
        }
    }
}
