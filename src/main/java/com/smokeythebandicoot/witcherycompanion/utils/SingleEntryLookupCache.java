package com.smokeythebandicoot.witcherycompanion.utils;

/**
 * Single-entry memo for "input -> recipe result" lookups that are performed over and over for the same input
 * (for example by a tile entity that asks for the same smelting result every tick).
 * <p>
 * The key is made of an identity-compared object (the Item) and an int (the metadata). An entry is only reused if:
 * 1. the key matches (Item by reference, metadata by value)
 * 2. the fingerprint (a cheap value that changes when the recipe set changes, e.g. the recipe map size) matches
 * 3. the entry is younger than the time-to-live, so that a stale entry is always bounded in time
 * Otherwise, the caller has to perform the normal lookup and store the result.
 */
public class SingleEntryLookupCache<K, V> {

    private boolean filled = false;
    private K key;
    private int meta;
    private int fingerprint;
    private long storedAt;
    private V value;

    /** Returns true if the cache holds a valid entry for the given key at the given time */
    public boolean isHit(K key, int meta, int fingerprint, long now, long ttl) {
        return this.filled
                && this.key == key
                && this.meta == meta
                && this.fingerprint == fingerprint
                && now >= this.storedAt
                && now - this.storedAt < ttl;
    }

    /** Returns the cached value. Only meaningful right after {@link #isHit} returned true */
    public V get() {
        return this.value;
    }

    public void store(K key, int meta, int fingerprint, long now, V value) {
        this.filled = true;
        this.key = key;
        this.meta = meta;
        this.fingerprint = fingerprint;
        this.storedAt = now;
        this.value = value;
    }

    public void invalidate() {
        this.filled = false;
        this.key = null;
        this.value = null;
    }

}
