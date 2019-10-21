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
public abstract class Item implements Serializable{
    protected ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }
    
    public ItemType getItemType() {
        return type;
    }
    
    public abstract double getMass();
    public abstract Item multiply(double times);
    
    @Override
    public abstract Item clone();

    public Item itemType(ItemType type) {
        Item item = this.clone();
        item.type = type;
        return item;
    }
    
}
