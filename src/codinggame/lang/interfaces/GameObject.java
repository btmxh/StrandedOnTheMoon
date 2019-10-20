/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.handlers.CommandHandler;
import codinggame.lang.CommandBlock;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class GameObject {
    GameState game;
    CommandHandler executor;
    CommandBlock currentBlock;
    Robot robot;

    public GameObject(GameState game, Robot robot) {
        this.game = game;
        this.robot = robot;
        this.executor = game.getCommandHandler();
        this.currentBlock = new CommandBlock(game, currentBlock, robot, executor);
    }
    
    public void println(Object obj) {
        game.getUIHandler().println(obj == null? "null":obj.toString());
    }
    
    public void print(Object obj) {
        game.getUIHandler().print(obj == null? "null":obj.toString());
    }
    
    void lock() {
        Object lock = robot.getLock();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
