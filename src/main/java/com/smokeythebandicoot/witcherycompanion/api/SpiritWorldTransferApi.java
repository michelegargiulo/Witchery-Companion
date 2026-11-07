package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.accessors.spiritworld.ISpiritPlayerDataAccessor;
import com.smokeythebandicoot.witcherycompanion.api.spiritworld.IExtraSlotsProvider;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.DimensionTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.ExtraSlotsTransfer;
import com.smokeythebandicoot.witcherycompanion.utils.ItemMetaSet;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryBrewItems;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import net.msrandom.witchery.util.WitcheryUtils;
import net.msrandom.witchery.world.dimension.spirit.SpiritWorldData;

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

    /** Providers of extra inventory slots (Baubles...). See {@link IExtraSlotsProvider} */
    private static final List<IExtraSlotsProvider> extraSlotsProviders = new ArrayList<>();

    private static final String EXTRA_PROVIDER = "Provider";
    private static final String EXTRA_SLOT = "Slot";
    private static final String EXTRA_STACK = "Stack";

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
            enteringItems.add(item, effectiveMeta(item, 0));
        }
        for (Item item : nativeExiting) {
            exitingItems.add(item, effectiveMeta(item, 0));
        }
    }


    /// REGISTRATION

    /** The stack will be carried from the Overworld into the Spirit World. Returns false if already registered */
    public static boolean addEnteringItem(ItemStack stack) {
        return !stack.isEmpty() && enteringItems.add(stack.getItem(), effectiveMeta(stack));
    }

    /** The stack will not be carried into the Spirit World anymore. Returns false if it was not registered */
    public static boolean removeEnteringItem(ItemStack stack) {
        return !stack.isEmpty() && enteringItems.remove(stack.getItem(), effectiveMeta(stack));
    }

    /** The stack will be carried from the Spirit World to the Overworld. Returns false if already registered */
    public static boolean addExitingItem(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.add(stack.getItem(), effectiveMeta(stack));
    }

    /** The stack will not be carried out of the Spirit World anymore. Returns false if it was not registered */
    public static boolean removeExitingItem(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.remove(stack.getItem(), effectiveMeta(stack));
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
        return !stack.isEmpty() && enteringItems.contains(stack.getItem(), effectiveMeta(stack));
    }

    /** Returns true if the stack is carried from the Spirit World to the Overworld */
    public static boolean canExit(ItemStack stack) {
        return !stack.isEmpty() && exitingItems.contains(stack.getItem(), effectiveMeta(stack));
    }

    /** Same as {@link #canEnter(ItemStack)}, for an item and a metadata */
    public static boolean canEnter(Item item, int meta) {
        return item != null && enteringItems.contains(item, effectiveMeta(item, meta));
    }

    /** Same as {@link #canExit(ItemStack)}, for an item and a metadata */
    public static boolean canExit(Item item, int meta) {
        return item != null && exitingItems.contains(item, effectiveMeta(item, meta));
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
                set.contains(stack.getItem(), effectiveMeta(stack));
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


    /// EXTRA SLOTS

    /**
     * Registers the extra slots that a mod adds to the player. If extra slots are enabled in the config, what is in
     * them is treated as the rest of the inventory: it only crosses the barrier if the item is in the lists above.
     * A provider with the same id as an already registered one replaces it
     */
    public static void registerExtraSlotsProvider(IExtraSlotsProvider provider) {
        extraSlotsProviders.removeIf(p -> p.getId().equals(provider.getId()));
        extraSlotsProviders.add(provider);
    }

    /** The registered providers of extra slots */
    public static List<IExtraSlotsProvider> getExtraSlotsProviders() {
        return Collections.unmodifiableList(extraSlotsProviders);
    }

    /**
     * Takes out of the extra slots whatever is not allowed to cross the barrier and stores it with the data of the
     * side the player is leaving (Overworld data when entering, Spirit World data when exiting). Items that are
     * allowed to cross stay in their slot. Call it right before Witchery stores the inventory
     */
    public static void stashExtraSlots(EntityPlayer player, boolean entering) {
        String direction = entering ? "entering" : "exiting";
        if (!DimensionTweaks.spiritWorld_tweakExtraSlotsFollowTransferLists) {
            return;
        }

        NBTTagList target = null;
        for (IExtraSlotsProvider provider : extraSlotsProviders) {
            List<ExtraSlotsTransfer.Entry<ItemStack>> stashed = ExtraSlotsTransfer.stash(
                    new ProviderSlots(provider, player), entering ? SpiritWorldTransferApi::canEnter : SpiritWorldTransferApi::canExit);
            if (stashed.isEmpty()) {
                continue;
            }

            if (target == null) {
                // Entering: the Overworld stuff is stored with the original data. Exiting: the Spirit World stuff with the spirit data
                target = getStashHolder(player, entering, true).witcherycompanion$accessor$getExtraSlots();
            }
            for (ExtraSlotsTransfer.Entry<ItemStack> entry : stashed) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(EXTRA_PROVIDER, provider.getId());
                tag.setInteger(EXTRA_SLOT, entry.slot);
                tag.setTag(EXTRA_STACK, entry.stack.writeToNBT(new NBTTagCompound()));
                target.appendTag(tag);
            }
        }
    }

    /**
     * Puts back in the extra slots what was stored with the data of the side the player is going to (Spirit World
     * data when entering, Overworld data when exiting). What cannot go back to its slot goes in the inventory, and if
     * that is full it is dropped. Items of providers that are not registered anymore stay stored. Call it once
     * Witchery has swapped the inventory
     */
    public static void restoreExtraSlots(EntityPlayer player, boolean entering) {
        // The Spirit World data must not be created here: for Witchery, its existence means that the player has been there
        ISpiritPlayerDataAccessor holder = getStashHolder(player, !entering, false);
        if (holder == null) {
            return;
        }

        NBTTagList stored = holder.witcherycompanion$accessor$getExtraSlots();
        if (stored.tagCount() == 0) {
            return;
        }

        NBTTagList remaining = new NBTTagList();
        Map<String, List<ExtraSlotsTransfer.Entry<ItemStack>>> byProvider = new HashMap<>();
        for (int i = 0; i < stored.tagCount(); i++) {
            NBTTagCompound tag = stored.getCompoundTagAt(i);
            String id = tag.getString(EXTRA_PROVIDER);
            if (getExtraSlotsProvider(id) == null) {
                remaining.appendTag(tag);
                continue;
            }

            ItemStack stack = new ItemStack(tag.getCompoundTag(EXTRA_STACK));
            if (!stack.isEmpty()) {
                byProvider.computeIfAbsent(id, k -> new ArrayList<>())
                        .add(new ExtraSlotsTransfer.Entry<>(tag.getInteger(EXTRA_SLOT), stack));
            }
        }
        holder.witcherycompanion$accessor$setExtraSlots(remaining);

        List<ItemStack> overflow = new ArrayList<>();
        for (Map.Entry<String, List<ExtraSlotsTransfer.Entry<ItemStack>>> group : byProvider.entrySet()) {
            IExtraSlotsProvider provider = getExtraSlotsProvider(group.getKey());
            for (ExtraSlotsTransfer.Entry<ItemStack> entry : ExtraSlotsTransfer.restore(new ProviderSlots(provider, player), group.getValue())) {
                overflow.add(entry.stack);
            }
        }
        restore(player, overflow);
    }

    private static IExtraSlotsProvider getExtraSlotsProvider(String id) {
        for (IExtraSlotsProvider provider : extraSlotsProviders) {
            if (provider.getId().equals(id)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Where the stacks taken out of the extra slots are kept: with the Overworld data (original) or the Spirit World
     * data. Without create, null is returned if the Spirit World data does not exist yet
     */
    private static ISpiritPlayerDataAccessor getStashHolder(EntityPlayer player, boolean original, boolean create) {
        SpiritWorldData data = WitcheryUtils.getExtension(player).getSpiritData();
        if (original) {
            return (ISpiritPlayerDataAccessor) (Object) data.getOriginalData();
        }
        if (!create && !data.getHasSpiritData()) {
            return null;
        }
        return (ISpiritPlayerDataAccessor) (Object) data.getSpiritData();
    }

    /** Slots of a provider, for the generic transfer logic */
    private static class ProviderSlots implements ExtraSlotsTransfer.Slots<ItemStack> {
        private final IExtraSlotsProvider provider;
        private final EntityPlayer player;

        ProviderSlots(IExtraSlotsProvider provider, EntityPlayer player) {
            this.provider = provider;
            this.player = player;
        }

        @Override
        public int size() {
            return provider.getSlots(player);
        }

        @Override
        public ItemStack get(int slot) {
            // A copy, so that emptying the slot cannot alter what has been read
            return provider.getStack(player, slot).copy();
        }

        @Override
        public boolean isEmpty(ItemStack stack) {
            return stack.isEmpty();
        }

        @Override
        public void clear(int slot) {
            provider.setStack(player, slot, ItemStack.EMPTY);
        }

        @Override
        public boolean put(int slot, ItemStack stack) {
            return provider.setStack(player, slot, stack);
        }
    }


    /// MATCHING

    /** Metadata used for matching: the real one for items that use it as a subtype, the wildcard for all the others (damaged items included) */
    private static int effectiveMeta(ItemStack stack) {
        return effectiveMeta(stack.getItem(), stack.getMetadata());
    }

    private static int effectiveMeta(Item item, int meta) {
        return ItemMetaSet.effectiveMeta(item.getHasSubtypes() && !item.isDamageable(), meta);
    }
}
