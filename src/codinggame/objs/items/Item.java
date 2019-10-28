/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import java.io.Serializable;

/**
 *
 * @author Welcome
 */
public abstract class Item<T extends ItemType> implements Serializable{
    protected T type;

    public Item(T type) {
        this.type = type;
    }
    
    public T getItemType() {
        return type;
    }
    
    public abstract double getMass();
    public abstract Item<T> multiply(double times);
    
    @Override
    public abstract Item<T> clone();

    public Item itemType(ItemType type) {
        Item item = this.clone();
        item.type = type;
        return item;
    }
    
}
