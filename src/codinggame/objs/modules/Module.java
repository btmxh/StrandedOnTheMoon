/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.items.ItemType;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.utils.Utils;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.util.Pair;

/**
 *
 * @author Welcome
 */
public abstract class Module extends ItemType.Count implements Serializable{
    
    protected transient GameState game;
    private Class<? extends Robot>[] robotClasses;

    public Module(GameState game, String name, String path, Class<? extends Robot>... robotClasses) {
        super(path, name, 0);
        this.game = game;
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
    
    public void setGameState(GameState game) {
        this.game = game;
    }
    
    @Override
    public abstract Module clone();
}
