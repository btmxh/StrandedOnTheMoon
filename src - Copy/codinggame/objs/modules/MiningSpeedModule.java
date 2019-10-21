/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class MiningSpeedModule extends Module{
    
    private static final float DEFAULT_SCALE = 1.2f;
    
    private float scale = DEFAULT_SCALE;
    
    public MiningSpeedModule(GameState game) {
        super(game, "Mining Speed Module", ModuleTextures.MINING_SPEED_MODULE, MinerRobot.class);
    }

    public float getScale() {
        return scale;
    }
    
    @Override
    public MiningSpeedModule clone() {
        MiningSpeedModule module = new MiningSpeedModule(game);
        module.scale = scale;
        return module;
    }
}
