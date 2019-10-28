/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author Welcome
 */
public class ArrayMap<K, V> extends ArrayList<Pair<K, V>>{

    public V get(K key) {
        for (int i = 0; i < size(); i++) {
            Pair<K, V> pair = get(i);
            K k = pair.getKey();
            if(k == key) {
                return pair.getValue();
            } else if(key != null? key.equals(k):false) {
                return pair.getValue();
            }
        }
        return null;
    }
    
    public void put(K key, V value) {
        for (int i = 0; i < size(); i++) {
            Pair<K, V> pair = get(i);
            K k = pair.getKey();
            if(k == key) {
                pair = new Pair<>(key, value);
                set(i, pair);
                return;
            } else if(key != null? key.equals(k):false) {
                pair = new Pair<>(key, value);
                set(i, pair);
                return;
            }
        }
        add(new Pair<>(key, value));
    }
    
    public V getValue(int idx) {
        return get(idx).getValue();
    }
    
    public K getKey(int idx) {
        return get(idx).getKey();
    }

    public Set<K> keySet() {
        return stream().map(Pair::getKey).collect(Collectors.toSet());
    }
    
    public Set<Pair<K, V>> entrySet() {
        return new HashSet<>(this);
    }
    
    public List<V> values() {
        return stream().map(Pair::getValue).collect(Collectors.toList());
    }
    
}
