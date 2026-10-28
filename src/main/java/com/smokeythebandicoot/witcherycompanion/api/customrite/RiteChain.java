package com.smokeythebandicoot.witcherycompanion.api.customrite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The order of the rites in the books of W:R is not stored in a list: every rite names its neighbours in its data
 * ("sorting": {"left": ..., "right": ...}) and the whole set forms a single chain, from the rite without left to the
 * rite without right. A rite without neighbours does not belong to the chain and confuses the comparator that W:R uses
 * to sort the rites. This class models the chain with plain ids, so that custom rites can be inserted in it before W:R reads the data.
 */
public class RiteChain {

    public static class Node {
        public final String id;
        /** Group (section) of the rite: "namespace:folder" */
        public String group;
        public String left;
        public String right;

        public Node(String id, String group, String left, String right) {
            this.id = id;
            this.group = group;
            this.left = left;
            this.right = right;
        }
    }

    private final Map<String, Node> nodes = new LinkedHashMap<>();

    public void add(String id, String group, String left, String right) {
        nodes.put(id, new Node(id, group, left, right));
    }

    public boolean contains(String id) {
        return nodes.containsKey(id);
    }

    public Node get(String id) {
        return nodes.get(id);
    }

    public int size() {
        return nodes.size();
    }

    /** Ids from the first rite to the last, following the "right" links. Cycles and unknown ids end the walk */
    public List<String> order() {
        List<String> order = new ArrayList<>();
        Node head = null;
        for (Node node : nodes.values()) {
            // The head has no left and leads somewhere: rites with no neighbours at all are not part of the chain
            if (node.left == null && node.right != null) {
                head = node;
                break;
            }
        }
        Set<String> seen = new LinkedHashSet<>();
        Node current = head;
        while (current != null && seen.add(current.id)) {
            order.add(current.id);
            current = current.right == null ? null : nodes.get(current.right);
        }
        return order;
    }

    /** Last rite of the chain, or null if the chain is empty */
    public Node tail() {
        List<String> order = order();
        return order.isEmpty() ? null : nodes.get(order.get(order.size() - 1));
    }

    /** The last rite of the chain that belongs to the group, or null */
    public Node lastOfGroup(String group) {
        Node last = null;
        for (String id : order()) {
            Node node = nodes.get(id);
            if (group.equals(node.group)) last = node;
        }
        return last;
    }

    /**
     * Adds a new rite right after the anchor: anchor -> new -> (old right of the anchor).
     * @return the ids of the rites whose links changed (the anchor, the new rite and the old right neighbour, if any)
     */
    public List<String> insertAfter(String anchorId, String newId, String newGroup) {
        Node anchor = nodes.get(anchorId);
        if (anchor == null) throw new IllegalArgumentException("Unknown rite " + anchorId);

        List<String> changed = new ArrayList<>();
        Node oldRight = anchor.right == null ? null : nodes.get(anchor.right);

        Node inserted = new Node(newId, newGroup, anchor.id, oldRight == null ? null : oldRight.id);
        nodes.put(newId, inserted);
        anchor.right = newId;
        changed.add(anchor.id);
        changed.add(newId);
        if (oldRight != null) {
            oldRight.left = newId;
            changed.add(oldRight.id);
        }
        return changed;
    }

}
