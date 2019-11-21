/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.apis;

import codinggame.objs.Inventory;
import codinggame.objs.RobotInventory;
import codinggame.objs.items.Item;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class GameInventory<INV extends Inventory> extends GameObject{
    
    public static final int MODULES = RobotInventory.MODULES;
    
    protected INV inv;

    GameInventory(GameState game, Robot robot, INV inv) {
        super(game, robot);
        this.inv = inv;
    }
    
    public Item getItem(int slot) {
        return inv.getItems().get(slot).getValue();
    }
    
}
