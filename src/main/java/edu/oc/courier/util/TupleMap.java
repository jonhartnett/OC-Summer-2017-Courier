package edu.oc.courier.util;

import java.util.HashMap;
import java.util.Objects;

public class TupleMap<K1, K2, V> extends HashMap<Integer, V>{
    public V get(K1 key1, K2 key2) {
        return this.get(Objects.hash(key1, key2));
    }

    public V getOrDefault(K1 key1, K2 key2, V value){
        return this.getOrDefault(Objects.hash(key1, key2), value);
    }

    public V put(K1 key1, K2 key2, V v) {
        return super.put(Objects.hash(key1, key2), v);
    }
}
