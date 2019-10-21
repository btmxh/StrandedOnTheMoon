/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.handlers.CommandHandler;
import codinggame.lang.CommandBlock;
import codinggame.lang.commands.GoCommand;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class GameRobot extends GameObject{

    public GameRobot(GameState game, Robot robot) {
        super(game, robot);
    }
    
    public void go(int moveX, int moveY) {
        executor.execute(new GoCommand(game, null, robot, executor, new Vector2i(moveX, moveY)));
        super.lock();
    }
    
}
