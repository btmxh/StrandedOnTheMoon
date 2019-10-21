/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import com.lwjglwrapper.opengl.objects.Texture2D;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 *
 * @author Welcome
 */
public abstract class Module implements Serializable{
    
    protected transient GameState game;
    protected transient Texture2D texture;
    private String name;
    private Class<? extends Robot>[] robotClasses;

    public Module(GameState game, String name, Texture2D texture, Class<? extends Robot>... robotClasses) {
        this.game = game;
        this.name = name;
        this.texture = texture;
        this.robotClasses = robotClasses;
    }
    
    public boolean install(Robot robot, int slot) {
        if(canBeInstalled(robot)) {
            robot.getInventory().setModule(slot, this);
            return true;
        } else {
            return false;
        }
    }
    
    protected final boolean canBeInstalled(Robot robot) {
        return Stream.of(robotClasses).anyMatch((c) -> robot.instanceOf(c));
    }

    public String getName() {
        return name;
    }
    
    public void setGameState(GameState game) {
        this.game = game;
    }

    public Texture2D getTexture() {
        return texture;
    }
    
    @Override
    public abstract Module clone();
}
