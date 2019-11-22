/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * a simpler version of java.lang.Iterable
 * @author Welcome
 */
@FunctionalInterface
public interface ForEachable<T> {
    public void iterate(Consumer<T> consumer);
    
    public static void forEach(Object iterable, Consumer consumer) {
        if(iterable instanceof ForEachable) {
            ((ForEachable) iterable).iterate(consumer);
        } else if(iterable instanceof Iterable) {
            ((Iterable) iterable).forEach(consumer);
        } else throw new IllegalStateException("what did u bring upon this cursed land: " + iterable);
    }
    
    public static class LinkedList_ForEachable<T> extends LinkedList<T> implements ForEachable<T>{
        
        @Override
        public void iterate(Consumer<T> consumer) {
            super.forEach(consumer);
        }
        
    }
}
