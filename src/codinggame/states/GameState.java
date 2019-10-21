/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import codinggame.globjs.Camera;
import codinggame.globjs.RenderableTexture;
import codinggame.handlers.CommandHandler;
import codinggame.handlers.GameUIHandler;
import codinggame.handlers.MapHandler;
import codinggame.handlers.RobotHandler;
import codinggame.map.proceduralmap.chunkloading.ChunkThread;
import codinggame.objs.Clock;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.modules.ModuleTextures;
import codinggame.objs.modules.SolarPanelModule;
import codinggame.objs.robots.CraftingRobot;
import codinggame.objs.robots.MinerRobot;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.FBO;
import com.lwjglwrapper.opengl.objects.RBO;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.Logger;
import com.lwjglwrapper.utils.states.State;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author Welcome
 */
public class GameState extends State<CodingGame>{
    
//    public static final int TILE_PIXEL_WIDTH = 32, TILE_PIXEL_HEIGHT = 32;
    //Map
//    private GameMap map;
//    private MapRenderer renderer;
    //FBOs
    FBO gameFBO;
    Texture2D gameTexture;
    //Camera, Viewport
    public Viewport windowViewport;
    private Camera camera;
    //Handlers
    private MapHandler mapHandler;
    private RobotHandler robotHandler;
    private GameUIHandler gameUIHandler;
    private CommandHandler commandHandler;
    //Clock
    private Clock clock;
    
    private Object choosen;
    
    private InputProcessor inputProcessor;
    
    public GameState(CodingGame game) {
        super(game);
        ItemTypes.initTypes();
        ModuleTextures.init();
        inputProcessor = InputProcessor.GAME;
        LWJGL.window.getWindowCallbacks().getWindowCloseCallback().add(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                new Thread(() -> {
                    ChunkThread.stopThread();
                    robotHandler.writeRobots();
                    mapHandler.save(clock);
                }).start();
            }
        });
        
        clock = new Clock();
        
        windowViewport = LWJGL.window.getViewport();
        mapHandler = new MapHandler(this, clock);
        
        camera = new Camera();
        robotHandler = new RobotHandler(this, camera);
        
        if(!robotHandler.loadRobots(this)) {
            robotHandler.addRobot(new MinerRobot(this, new Vector2f(10.5f, 10.5f), "miner"), true);
            robotHandler.addRobot(new CraftingRobot(this, new Vector2f(7.5f, 5.5f), "crafter"), false);
        }
        robotHandler.getRobot("miner").getInventory().setModule(0, new SolarPanelModule(this, 0.2, 1));
        
        gameUIHandler = new GameUIHandler(LWJGL.window, this);
        commandHandler = new CommandHandler(this);
        
        gameFBO = new FBO(GL30.GL_FRAMEBUFFER);
        gameFBO.bind();
        
        RBO rbo = new RBO(GL30.GL_DEPTH24_STENCIL8, LWJGL.window.getWidth(), LWJGL.window.getHeight());
        gameFBO.attachRBO(GL30.GL_DEPTH_STENCIL_ATTACHMENT, rbo);
        gameTexture = gameFBO.createTexture();
        gameFBO.checkProgress(System.out, true);
        gameFBO.unbind();
    }

    @Override
    public void show() {
        
    }

    @Override
    public void update(float delta) {
        if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            inputProcessor = gameUIHandler.clickedOn()? InputProcessor.GAME_UI:InputProcessor.GAME;
        }
        
        gameUIHandler.tick(inputProcessor);
        robotHandler.update(inputProcessor);
        commandHandler.update(delta);
        
        mapHandler.update(camera);
        camera.update(inputProcessor == InputProcessor.GAME);
        clock.update(delta);
        
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_ESCAPE)) {
            switch (inputProcessor) {
                case GAME:      game.setStateByIndex(1);
                case GAME_UI:   gameUIHandler.closeInfoBar();
            }
        }
    }

    @Override
    public void render() {
        GLCalls.setClearColor(new IColor(0.1f));
        GLCalls.enable(GL11.GL_SCISSOR_TEST);
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GLCalls.enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        gameFBO.bind();
        GLCalls.setClearColor(new IColor(0.1f));
        GLCalls.enable(GL11.GL_SCISSOR_TEST);
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        
        GLCalls.enable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        LWJGL.graphics.begin();
        LWJGL.graphics.rect(0, 0, LWJGL.window.getWidth(), LWJGL.window.getHeight());
        LWJGL.graphics.fill(IColor.BLACK);
        LWJGL.graphics.end();
        
        mapHandler.render(!robotHandler.hoveringOnRobots() && inputProcessor == InputProcessor.GAME);
        windowViewport.set(LWJGL.window);
        
        robotHandler.render(LWJGL.graphics);
        windowViewport.updateScissor(LWJGL.graphics);
        LWJGL.graphics.strokeWeight(2);
        gameUIHandler.render(LWJGL.graphics);   //it will auto end the graphics
        gameFBO.unbind();
        
        new RenderableTexture(new Rectanglef(0, 0, LWJGL.window.getWidth(), LWJGL.window.getHeight()), gameTexture).render(true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    public RobotHandler getRobotHandler() {
        return robotHandler;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public Viewport getMapViewport() {
        return windowViewport;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public Clock getClock() {
        return clock;
    }

    public void select(Object obj) {
        this.choosen = obj;
    }

    public Object getSelectedObject() {
        return choosen;
    }

    public Camera getCamera() {
        return camera;
    }

    public GameUIHandler getUIHandler() {
        return gameUIHandler;
    }

    public void setProcessor(InputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }
    
}
