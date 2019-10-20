/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame;

import codinggame.globjs.RenderableTexture;
import codinggame.objs.items.ItemTypes;
import codinggame.ui.CodingArea;
import codinggame.states.GameState;
import codinggame.states.OptionState;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.utils.states.GameStateLoop;
import java.util.Random;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public class CodingGame extends GameStateLoop {
    
    private Random random = new Random();
    private long frameID;
    private GameState gs;
    private OptionState os;
    
    public CodingGame() {
    }

    @Override
    protected void init() throws Exception {
        super.init();
        CodingArea.initFont();
        RenderableTexture.init();
        LWJGL.currentLoop = this;
        
        gs = new GameState(this);
        os = new OptionState(this);
        setState(gs);
    }

    @Override
    protected void dispose() {
        ItemTypes.dispose();
    }

    @Override
    protected void render() throws Exception {
        frameID = random.nextLong();
        super.render();
    }

    public long getFrameID() {
        return frameID;
    }

    @Override
    protected void update(float delta) throws Exception {
        super.update(delta);
    }
    
    
    
    
    
    
    

    @Override
    protected void createWindow() {
        Window.init();
        
        window = new Window(1280, 720, "Coding Game", 60);
//        window.fitScreen();
//        window.fullScreen(true);
        window.create();
        
        window.setVisible(true);
        
        window.setToMainWindow();
        window.createNVGGraphics();
        window.createStage();
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new Thread(() -> {
//            while(true);
//        }).start();
        new CodingGame().run();
    }

    public GameState getGameState() {
        return gs;
    }
    
    public static CodingGame getInstance() {
        return (CodingGame) LWJGL.currentLoop;
    }
    
    //For testing purposes
    public static void debug(Object obj) {
        getInstance().getGameState().getUIHandler().println(obj == null?"null":obj.toString());
    }

}
