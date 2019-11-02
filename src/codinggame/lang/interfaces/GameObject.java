/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.CodingGame;
import codinggame.handlers.CommandHandler;
import codinggame.lang.CommandBlock;
import codinggame.lang.StopException;
import codinggame.lang.commands.WaitCommand;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.ui.codingarea.CodingFX;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class GameObject {
    GameState game;
    CommandHandler executor;
    Robot robot;

    public GameObject(GameState game, Robot robot) {
        this.game = game;
        this.robot = robot;
        this.executor = game.getCommandHandler();
    }
    
    public void println(Object obj) {
        print((obj == null? "null":obj.toString()) + "\n");
    }
    
    public void print(Object obj) {
        CodingFX.currentController.print(obj == null? "null":obj.toString());
        testInterupt();
    }
    
    public void waitFor(float time) {
        executor.execute(new WaitCommand(game, null, robot, executor, time));
        lock();
        testInterupt();
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
    
    public void addTime(long time) {
        game.getClock().update(time / 1000f);
    }
    
    void testInterupt() {
        if(robot.stopped()) {
            throw new StopException();
        }
    }
}
