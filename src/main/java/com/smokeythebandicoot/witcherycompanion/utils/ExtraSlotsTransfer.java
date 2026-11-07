package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Logic for moving the contents of extra inventory slots (Baubles, ...) out of the way and back, generic over the
 * stack type so that it does not depend on Minecraft types.
 */
public class ExtraSlotsTransfer {

    /** Access to a group of slots */
    public interface Slots<T> {
        int size();
        T get(int slot);
        boolean isEmpty(T stack);
        /** Empties the slot */
        void clear(int slot);
        /** Tries to put the stack in the slot. Returns false if the slot refused it (and so nothing changed) */
        boolean put(int slot, T stack);
    }

    /** A stack and the slot it was taken from */
    public static final class Entry<T> {
        public final int slot;
        public final T stack;

        public Entry(int slot, T stack) {
            this.slot = slot;
            this.stack = stack;
        }
    }

    /**
     * Empties every slot whose stack is not allowed to cross, and returns what was taken out. The stacks that are
     * allowed to cross are left where they are
     */
    public static <T> List<Entry<T>> stash(Slots<T> slots, Predicate<T> canCross) {
        List<Entry<T>> stashed = new ArrayList<>();
        for (int slot = 0; slot < slots.size(); slot++) {
            T stack = slots.get(slot);
            if (stack == null || slots.isEmpty(stack) || canCross.test(stack)) {
                continue;
            }
            stashed.add(new Entry<>(slot, stack));
            slots.clear(slot);
        }
        return stashed;
    }

    /**
     * Puts every entry back in its slot. An entry is not placed if its slot does not exist anymore, is occupied or
     * refuses the stack. Returns those entries, so that the caller can find them another place
     */
    public static <T> List<Entry<T>> restore(Slots<T> slots, List<Entry<T>> entries) {
        List<Entry<T>> notPlaced = new ArrayList<>();
        for (Entry<T> entry : entries) {
            boolean placed = false;
            if (entry.slot >= 0 && entry.slot < slots.size()) {
                T current = slots.get(entry.slot);
                if ((current == null || slots.isEmpty(current)) && slots.put(entry.slot, entry.stack)) {
                    placed = true;
                }
            }
            if (!placed) {
                notPlaced.add(entry);
            }
        }
        return notPlaced;
    }
}
