/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class SpeedModule extends Module{
    
    private static final String TEXTURE_PATH = "/items/speed_module.png";
    
    private static final float DEFAULT_SCALE = 10.2f;
    
    private float scale = DEFAULT_SCALE;
    
    public SpeedModule(GameState game) {
        super(game, "Speed Module", TEXTURE_PATH, Robot.class);
    }

    public float getScale() {
        return scale;
    }
    
    @Override
    public SpeedModule clone() {
        SpeedModule module = new SpeedModule(game);
        module.scale = scale;
        return module;
    }
}
