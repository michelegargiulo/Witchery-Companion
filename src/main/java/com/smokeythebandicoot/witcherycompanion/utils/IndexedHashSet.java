package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class IndexedHashSet<T> {
    private final ArrayList<T> list;
    private final HashMap<T, Integer> map;
    private final Random rand;

    public IndexedHashSet() {
        list = new ArrayList<>();
        map = new HashMap<>();
        rand = new Random();
    }

    public boolean add(T value) {
        if (map.containsKey(value)) {
            return false;
        }
        map.put(value, list.size());
        list.add(value);
        return true;
    }

    public boolean remove(T value) {
        if (!map.containsKey(value)) {
            return false;
        }
        int index = map.get(value);
        if (index < list.size() - 1) {
            T lastElement = list.get(list.size() - 1);
            list.set(index, lastElement);
            map.put(lastElement, index);
        }
        map.remove(value);
        list.remove(list.size() - 1);
        return true;
    }

    public boolean contains(T value) {
        return map.containsKey(value);
    }

    public T getRandom() {
        return list.get(rand.nextInt(list.size()));
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        map.clear();
        list.clear();
    }
}

