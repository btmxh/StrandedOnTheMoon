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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Welcome
 */
public class CodingGame extends GameStateLoop {
    
    private Random random = new Random();
    private long frameID;
    public GameState gs;
    public OptionState os;
    
    private final List<Runnable> queries = new ArrayList<>();
    
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
        synchronized (queries) {
            if(!queries.isEmpty()) {
                queries.get(0).run();
            }
        }
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
        getInstance().gs.getUIHandler().println(obj == null?"null":obj.toString());
    }
    
    public void execInMainThread(Runnable runnable) {
        queries.add(runnable);
    }

}
