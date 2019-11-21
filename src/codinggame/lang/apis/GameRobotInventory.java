/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.apis;

import codinggame.objs.RobotInventory;
import codinggame.objs.items.Item;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.modules.Module;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class GameRobotInventory extends GameInventory<RobotInventory>{
    
    GameRobotInventory(GameState game, Robot robot, RobotInventory inv) {
        super(game, robot, inv);
    }
    
    public Module getModule(int slot) {
        return inv.getModules()[slot];
    }
    
    public Item getEquipment(EquipmentSlot slot) {
        return inv.getEquipment(slot);
    }
    
}
