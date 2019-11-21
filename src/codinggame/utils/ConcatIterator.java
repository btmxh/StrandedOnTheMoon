package codinggame.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


public class ConcatIterator<T> implements Iterator<T> {

    private final List<Iterable<T>> iterables;
    private Iterator<T> current;

    @SafeVarargs
    public ConcatIterator(final Iterable<T>... iterables) {
        this(Arrays.asList(iterables));
    }
    
    public ConcatIterator(List<Iterable<T>> iterables) {
        this.iterables = new LinkedList<>(iterables);
        for (Iterator<Iterable<T>> it = this.iterables.iterator(); it.hasNext();) {
            if(it.next() == null)   it.remove();
        }
    }

    @Override
    public boolean hasNext() {
        checkNext();
        return current != null && current.hasNext();
    }

    @Override
    public T next() {
        checkNext();
        if (current == null || !current.hasNext()) throw new NoSuchElementException();
        return current.next();
    }

    @Override
    public void remove() {
        if (current == null) throw new IllegalStateException();
        current.remove();
    }

    private void checkNext() {
        while ((current == null || !current.hasNext()) && !iterables.isEmpty()) {
            current = iterables.remove(0).iterator();
        }
    }
    
    @SafeVarargs
    public static <T> Iterable<T> concat(final Iterable<T>... iterables) {
        return () -> new ConcatIterator<>(iterables);
    }
    
    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>(Arrays.asList("abc", "xyz"));
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>(Arrays.asList("mnp"));
        
        Iterable<String> concat = concat(list1, list2, list3);
        
        concat.forEach(System.out::println);
        
        list2.add("uvw");
        
        concat.forEach(System.out::println);
        
    }

}