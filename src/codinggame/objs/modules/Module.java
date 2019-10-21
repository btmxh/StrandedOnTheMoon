/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

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
public abstract class Module implements Serializable{
    
    private static Map<String, Pair<Texture2D, NVGImage>> textures = new HashMap<>();
    
    protected transient GameState game;
    protected transient Texture2D texture;
    protected transient NVGImage nanoVGTexture;
    private String name;
    private Class<? extends Robot>[] robotClasses;

    public Module(GameState game, String name, String path, Class<? extends Robot>... robotClasses) {
        this.game = game;
        this.name = name;
        Pair<Texture2D, NVGImage> pair = textures.get(path);
        if(pair == null) {
            this.texture = new Texture2D(TextureData.fromResource(Module.class, path));
            try {
                this.nanoVGTexture = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(Module.class.getResourceAsStream(path), 8 * 1024), 0);
            } catch (IOException ex) {
                Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
            }
            textures.put(path, new Pair<>(texture, nanoVGTexture));
        } else {
            this.texture = pair.getKey();
            this.nanoVGTexture = pair.getValue();
        }
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

    public NVGImage getNanoVGTexture() {
        return nanoVGTexture;
    }
}
