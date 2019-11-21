/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame;

import aurelienribon.tweenengine.TweenManager;
import codinggame.globjs.RenderableTexture;
import codinggame.handlers.AudioHandler;
import codinggame.objs.items.ItemTypes;
import codinggame.states.GameState;
import codinggame.states.OptionState;
import codinggame.states.TestState;
import codinggame.ui.codingarea.CodingFX;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.utils.states.GameStateLoop;
import com.lwjglwrapper.utils.states.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Welcome
 */
public class CodingGame extends GameStateLoop {
    
    private Random random = new Random();
    private long frameID;
    public GameState gs;
    public OptionState os;
    public AudioHandler audioHandler;
    public TweenManager tweenManager;
    public TestState test;
    
    private final List<Runnable> queries = new ArrayList<>();
    
    public CodingGame() {
    }

    @Override
    protected void init() throws Exception {
        super.init();
        RenderableTexture.init();
        LWJGL.currentLoop = this;
        tweenManager = new TweenManager();
        
        gs = new GameState(this);
        os = new OptionState(this);
        test = new TestState(this);
        audioHandler = new AudioHandler();
        setState(gs);
    }

    @Override
    protected void dispose() {
        super.dispose();
        ItemTypes.dispose();
        audioHandler.dispose();
        System.exit(46290);
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
        synchronized (queries) {
            if(!queries.isEmpty()) {
                queries.get(0).run();
            }
        }
        tweenManager.update(delta);
    }
    
    
    
    
    
    
    

    @Override
    protected void createWindow() {
        Window.init();
        
        window = new Window(1280, 720, "Coding Game", 60){
            @Override
            protected void configWindow() {
                super.configWindow();
                windowHint(GLFW.GLFW_SAMPLES, 4);
                windowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GL11.GL_TRUE); 
            }
            
        };
//        window.fitScreen();
//        window.fullScreen(true);
        window.create();
        
        window.setVisible(true);
        
        window.setToMainWindow();
        window.createNVGGraphics();
        window.createStage();
    }

    
    
    public static String arg;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length > 0) {
            arg = args[0];
        }
        new CodingGame().run();
    }

    public GameState getGameState() {
        return gs;
    }
    
    public OptionState getOptionState() {
        return os;
    }
    
    public static CodingGame getInstance() {
        return (CodingGame) LWJGL.currentLoop;
    }
    
    public static void debug(Object obj) {
        CodingFX.currentController.println(obj == null?"null":obj.toString());
    }
    
    public void execInMainThread(Runnable runnable) {
        queries.add(runnable);
    }


}
