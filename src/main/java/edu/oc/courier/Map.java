package edu.oc.courier;

import java.util.HashMap;

public class Map<K> {
    private HashMap<K, Node<K>> nodes = new HashMap<>();

    public void add(K key){
        if(nodes.containsKey(key))
            throw new RuntimeException("Name " + key + " already in use.");
        nodes.put(key, new Node<>(key));
    }

    public void setLink(K key, K key2, double cost){
        Node<K> n1 = nodes.get(key);
        Node<K> n2 = nodes.get(key2);
        n1.link(n2, cost);
        n2.link(n1, cost);
    }

    public void removeLink(K key, K key2){
        this.setLink(key, key2, Double.POSITIVE_INFINITY);
    }

    public void setOneWayLink(K src, K dest, double cost){
        nodes.get(src).link(nodes.get(dest), cost);
    }

    public void removeOneWayLink(K src, K dest){
        this.setOneWayLink(src, dest, Double.POSITIVE_INFINITY);
    }

    public Route<K> getRoute(K start, K end){
        return nodes.get(start).getRoute(nodes.get(end));
    }
}
