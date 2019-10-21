/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.openhft.compiler.CompilerUtils;

/**
 *
 * @author Welcome
 */
public class JavaParser {

    private GameState game;
    
    public JavaParser(GameState game) {
        this.game = game;
    }
    
    public Runnable parse(String block) {
        System.out.println(block);
        try {
            Class clazz = CompilerUtils.CACHED_COMPILER.loadFromJava(new URLClassLoader(new URL[0]), "codinggame.miner", block);
            return () -> {
                try {
                    Method main = clazz.getMethod("main");
                    if(main != null) {
                        main.setAccessible(true);
                        Object inst = clazz.getConstructor(GameState.class, Robot.class).newInstance(game, game.getRobotHandler().getCurrentRobot());
                        main.invoke(inst);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Completed Yay");
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JavaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ()->{};
    }
    
}
