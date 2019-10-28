/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.robots;

import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.items.equipments.Hoe;
import codinggame.states.GameState;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class FarmingRobot extends Robot{
    public static final String NAME = "Farming Robot";
    
    public FarmingRobot(GameState game, Vector2f position, String name) {
        super(game, position, name);
        inventory.equip(EquipmentSlot.HOE, new Hoe(ItemTypes.OLD_HOE));
        
    }
    
}
