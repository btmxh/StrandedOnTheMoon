/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.objs.Inventory;
import codinggame.objs.RobotInventory;
import codinggame.objs.items.Item;

/**
 *
 * @author Welcome
 */
public class GameInventory<I extends Inventory> {
    
    public static final int MODULES = RobotInventory.MODULES;
    
    protected I inv;

    GameInventory(I inv) {
        this.inv = inv;
    }
    
    public Item getItem(int slot) {
        return inv.getItems().get(slot).getValue();
    }
    
}
