package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of (item, metadata) pairs to a value, with the same wildcard rules as {@link ItemMetaSet}. It is generic over the
 * item type so that its logic does not depend on Minecraft types. NBT is never taken into account.
 * - An entry registered with {@link ItemMetaSet#WILDCARD} matches every metadata of that item
 * - Looking up a metadata returns the exact entry if present, otherwise the wildcard entry
 * - Removing an item with {@link ItemMetaSet#WILDCARD} removes every entry of that item
 * - Removing an item with a specific metadata only removes that exact entry (a registered wildcard stays)
 */
public class ItemMetaMap<T, V> {

    private final Map<T, Map<Integer, V>> entries = new HashMap<>();

    /** Sets the value of the entry, replacing the previous one. Returns the previous value, if any */
    public V put(T item, int meta, V value) {
        return entries.computeIfAbsent(item, k -> new HashMap<>()).put(meta, value);
    }

    /** Removes the entry. Returns true if the map was modified */
    public boolean remove(T item, int meta) {
        Map<Integer, V> metas = entries.get(item);
        if (metas == null) {
            return false;
        }

        boolean modified;
        if (meta == ItemMetaSet.WILDCARD) {
            modified = !metas.isEmpty();
            metas.clear();
        } else {
            modified = metas.containsKey(meta);
            metas.remove(meta);
        }

        if (metas.isEmpty()) {
            entries.remove(item);
        }
        return modified;
    }

    /** Returns the value for the (item, meta) pair: the exact entry if present, otherwise the wildcard one. Or null */
    public V get(T item, int meta) {
        Map<Integer, V> metas = entries.get(item);
        if (metas == null) {
            return null;
        }
        V value = metas.get(meta);
        return value != null ? value : metas.get(ItemMetaSet.WILDCARD);
    }

    /** Removes everything */
    public void clear() {
        entries.clear();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
