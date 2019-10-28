/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.Clock;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class SolarPanelModule extends Module{
    
    private static final String TEXTURE_PATH = "/items/solar_module.png";
    
    private double chance;
    private int amount;

    public SolarPanelModule(GameState game, double chance, int amount) {
        super(game, "Solar Panel Module", TEXTURE_PATH, Robot.class);
        this.chance = chance;
        this.amount = amount;
    }
    
    public void update(Robot robot, float delta) {
        Clock clock = game.getClock();
        int hour = clock.getHour();
        float timeFactor = Math.abs(12 - hour) / 7.0f;
        if(timeFactor > 1)  return;
        final float BASE = 0.5f;
        float chanceScale = BASE + (1 - BASE) * timeFactor;
        if(Math.random() * chanceScale < chance * delta) {
            robot.getBattery().increase(amount);
        }
    }
    
    @Override
    public SolarPanelModule clone() {
        return new SolarPanelModule(game, chance, amount);
    }
    
}
