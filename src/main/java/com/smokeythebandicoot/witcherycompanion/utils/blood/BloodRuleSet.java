package com.smokeythebandicoot.witcherycompanion.utils.blood;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A set of rules that associates creatures to a BloodProfile. A creature is matched by, in order of priority:
 * 1. its exact registry name (for example minecraft:cow)
 * 2. its namespace (mod id), with rules registered as modid:*
 * 3. its class hierarchy (first registered class that the creature is an instance of)
 */
public class BloodRuleSet {

    private final Map<String, BloodProfile> byId = new ConcurrentHashMap<>();
    private final Map<String, BloodProfile> byNamespace = new ConcurrentHashMap<>();
    private final List<ClassRule> byClass = new CopyOnWriteArrayList<>();

    private static final class ClassRule {
        final Class<?> clazz;
        final BloodProfile profile;

        ClassRule(Class<?> clazz, BloodProfile profile) {
            this.clazz = clazz;
            this.profile = profile;
        }
    }


    public void putId(String id, BloodProfile profile) {
        byId.put(id, profile);
    }

    public void putNamespace(String namespace, BloodProfile profile) {
        byNamespace.put(namespace, profile);
    }

    /** Registering the same class again replaces its profile (keeping its priority) **/
    public synchronized void putClass(Class<?> clazz, BloodProfile profile) {
        for (int i = 0; i < byClass.size(); i++) {
            if (byClass.get(i).clazz == clazz) {
                byClass.set(i, new ClassRule(clazz, profile));
                return;
            }
        }
        byClass.add(new ClassRule(clazz, profile));
    }

    public void clear() {
        byId.clear();
        byNamespace.clear();
        byClass.clear();
    }

    public boolean isEmpty() {
        return byId.isEmpty() && byNamespace.isEmpty() && byClass.isEmpty();
    }

    /**
     * Finds the profile of a creature
     * @param id registry name of the creature, as namespace:path. Can be null if the creature is not registered
     * @param clazz class of the creature
     * @return the profile, BloodProfile.BLOCKED if the creature is explicitly blocked, null if no rule matches
     */
    public BloodProfile match(String id, Class<?> clazz) {
        if (id != null) {
            BloodProfile profile = byId.get(id);
            if (profile != null) return profile;

            int colon = id.indexOf(':');
            if (colon > 0) {
                profile = byNamespace.get(id.substring(0, colon));
                if (profile != null) return profile;
            }
        }
        if (clazz != null) {
            for (ClassRule rule : byClass) {
                if (rule.clazz.isAssignableFrom(clazz)) return rule.profile;
            }
        }
        return null;
    }

}
