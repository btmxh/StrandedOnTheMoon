/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class Game {
    public static void set(GameState game) {
        Game.game = game;
    } 
    
    private static GameState game;
    
    public static Robot getRobot(String name) {
        return game.getRobotHandler().getRobot(name);
    }
    
    public static void pause() {
        game.getGame().setStateByIndex(1);
    }
    
    public static void exit() {
        game.getGame().exit();
    }
}