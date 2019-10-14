/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.robots;

import codinggame.states.GameState;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class CraftingRobot extends Robot{
    public static final String NAME = "Crafting Robot";
    
    public CraftingRobot(GameState game, Vector2f position, String name) {
        super(game, position, name);
    }
    
}
