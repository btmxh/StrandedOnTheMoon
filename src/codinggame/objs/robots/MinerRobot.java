/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.robots;

import codinggame.objs.items.ItemTypes;
import static codinggame.objs.items.ItemTypes.OLD_DRILL;
import codinggame.objs.items.equipments.Drill;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.states.GameState;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class MinerRobot extends Robot{
    public static final String NAME = "Miner Robot";
    
    public MinerRobot(GameState game, Vector2f position, String name) {
        super(game, position, name);
        inventory.equip(EquipmentSlot.DRILL, new Drill(ItemTypes.OLD_DRILL));
    }

    public void setDrill(Drill drill) {
        inventory.equip(EquipmentSlot.DRILL, drill);
    }

    public Drill getDrill() {
        return (Drill) inventory.getEquipment(EquipmentSlot.DRILL);
    }
    
}
