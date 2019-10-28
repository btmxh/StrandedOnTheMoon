/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.objs.items.Item;

/**
 *
 * @author Welcome
 */
public class GameItem<I extends Item> {
    protected I item;

    GameItem(I item) {
        this.item = item;
    }
    
    public String getName() {
        return item.getItemType().getName();
    }
    
    public double getMass() {
        return item.getMass();
    }
}
