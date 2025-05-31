package com.smokeythebandicoot.witcherycompanion.utils;

import java.util.*;

public class GroupedSet<T> {
    private Map<Integer, List<T>> groups;
    private Map<T, Integer> elementIndex;
    private List<T> elements;
    private Random random;

    public GroupedSet(Random random) {
        groups = new HashMap<>();
        elementIndex = new HashMap<>();
        elements = new ArrayList<>();
        this.random = random;
    }

    public void add(T value) {
        add(value, null);
    }

    public void add(T value, Integer param) {
        groups.putIfAbsent(param, new ArrayList<>());
        groups.get(param).add(value);
        elementIndex.put(value, elements.size());
        elements.add(value);
    }

    public void remove(T value) {
        if (!elementIndex.containsKey(value)) return;
        int idx = elementIndex.get(value);
        T lastElement = elements.get(elements.size() - 1);
        elements.set(idx, lastElement);
        elementIndex.put(lastElement, idx);
        elements.remove(elements.size() - 1);
        elementIndex.remove(value);
        // Remove e from its parameter's list
        for (Map.Entry<Integer, List<T>> entry : groups.entrySet()) {
            if (entry.getValue().remove(value)) break;
        }
    }

    public boolean contains(T value) {
        return elementIndex.containsKey(value);
    }

    public int size() {
        return elements.size();
    }

    public boolean contains(T value, Integer dim) {
        List<T> group = groups.get(dim);
        List<T> defaultGroup = groups.get(null);
        return (group != null && group.contains(value)) || (defaultGroup != null && defaultGroup.contains(value));
    }

    public int size(Integer dim) {
        List<T> group = groups.get(dim);
        List<T> defaultGroup = groups.get(null);
        return (group == null ? 0 : group.size()) + (defaultGroup == null ? 0 : defaultGroup.size());
    }

    public void clear() {
        groups.clear();
        elementIndex.clear();
        elements.clear();
    }

    public T getRandom(Integer dim) {
        List<T> generalGroup = groups.get(null);
        if (dim != null) {
            List<T> group = groups.get(dim);
            if (group == null) {
                return getRandom(null);
            }
            int size = group.size() + generalGroup.size();
            if (size == 0) return null;
            int randomInt = random.nextInt(size);
            if (randomInt < group.size()) {
                return group.get(randomInt);
            } else {
                return generalGroup.get(randomInt - group.size());
            }
        } else {
            if (generalGroup.isEmpty()) return null;
            return generalGroup.get(random.nextInt(generalGroup.size()));
        }
    }

    public Set<T> toSet() {
        return new HashSet<>(elements);
    }
}
