package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Set of (item, metadata) pairs that supports the wildcard metadata. It is generic over the item type so that its
 * logic does not depend on Minecraft types. NBT is never taken into account.
 * - An entry registered with {@link #WILDCARD} matches every metadata of that item
 * - Removing an item with {@link #WILDCARD} removes every entry of that item
 * - Removing an item with a specific metadata only removes that exact entry (a registered wildcard stays)
 */
public class ItemMetaSet<T> {

    /** Same value as OreDictionary.WILDCARD_VALUE */
    public static final int WILDCARD = 32767;

    private final Map<T, Set<Integer>> entries = new HashMap<>();

    /** Adds the entry. Returns true if the set was modified */
    public boolean add(T item, int meta) {
        return entries.computeIfAbsent(item, k -> new HashSet<>()).add(meta);
    }

    /** Removes the entry. Returns true if the set was modified */
    public boolean remove(T item, int meta) {
        Set<Integer> metas = entries.get(item);
        if (metas == null) {
            return false;
        }

        boolean modified;
        if (meta == WILDCARD) {
            modified = !metas.isEmpty();
            metas.clear();
        } else {
            modified = metas.remove(meta);
        }

        if (metas.isEmpty()) {
            entries.remove(item);
        }
        return modified;
    }

    /** Returns true if the (item, meta) pair is matched, either exactly or by a wildcard entry */
    public boolean contains(T item, int meta) {
        Set<Integer> metas = entries.get(item);
        return metas != null && (metas.contains(meta) || metas.contains(WILDCARD));
    }

    /** Returns true if at least one entry of the item is registered, regardless of its metadata */
    public boolean containsItem(T item) {
        return entries.containsKey(item);
    }

    /** Removes everything */
    public void clear() {
        entries.clear();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /** Returns a snapshot of the registered items */
    public Set<T> items() {
        return new HashSet<>(entries.keySet());
    }
}
