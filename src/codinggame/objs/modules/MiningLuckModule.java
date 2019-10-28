/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.robots.MinerRobot;
import codinggame.states.GameState;
import java.io.IOException;

/**
 *
 * @author Welcome
 */
public class MiningLuckModule extends Module{
    
    private static final float DEFAULT_SCALE = 1.2f;
    private static final String TEXTURE_PATH = "/items/mine_luck_module.png";
    
    private float scale = DEFAULT_SCALE;
    
    public MiningLuckModule(GameState game) {
        super(game, "Mining Luck Module", TEXTURE_PATH, MinerRobot.class);
    }

    public float getScale() {
        return scale;
    }

    @Override
    public MiningLuckModule clone() {
        MiningLuckModule module = new MiningLuckModule(game);
        module.scale = scale;
        return module;
    }
}
