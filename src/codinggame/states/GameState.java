/*
 * To change this license header, mouseReleased License Headers in Project Properties.
 * To change this template file, mouseReleased Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import codinggame.globjs.Camera;
import codinggame.globjs.RenderableTexture;
import codinggame.handlers.CommandHandler;
import codinggame.handlers.GameUIHandler;
import codinggame.handlers.MapHandler;
import codinggame.handlers.ObjectChooseHandler;
import codinggame.handlers.RobotHandler;
import codinggame.map.MapCell;
import codinggame.map.proceduralmap.chunkloading.ChunkThread;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.handlers.GameHandler;
import codinggame.handlers.SurvivalHandler;
import codinggame.objs.Clock;
import codinggame.objs.buildings.Building;
import codinggame.objs.buildings.Greenhouse;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.modules.SolarPanelModule;
import codinggame.objs.robots.CraftingRobot;
import codinggame.objs.robots.FarmingRobot;
import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.ui.codingarea.CodingFX;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.FBO;
import com.lwjglwrapper.opengl.objects.RBO;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.utils.colors.StaticColor;
import com.lwjglwrapper.utils.Logger;
import com.lwjglwrapper.utils.states.State;
import javafx.application.Platform;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author Welcome
 */
public class GameState extends State<CodingGame>{
    
    //FBOs
    boolean recreateFBO = true;
    FBO gameFBO;
    Texture2D gameTexture;
    //Camera, Viewport
    public Viewport windowViewport;
    //Handlers
    private GameHandler gameHandler;
    private MapHandler mapHandler;
    private RobotHandler robotHandler;
    private GameUIHandler gameUIHandler;
    private CommandHandler commandHandler;
    private ObjectChooseHandler chooseHandler;
    private SurvivalHandler survivalHandler;
    //Clock
    private Clock clock;
    
    private InputProcessor inputProcessor;
    
    
    public GameState(CodingGame game) {
        super(game);
        Logger.enable = true;
        ItemTypes.initTypes();
        inputProcessor = InputProcessor.GAME;
        LWJGL.window.getWindowCallbacks().getWindowCloseCallback().add(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                new Thread(() -> {
                    ChunkThread.stopThread();
                    robotHandler.writeRobots();
                    mapHandler.save(clock);
                    Platform.exit();
                }).start();
            }
        });
        
        clock = new Clock();
        
        windowViewport = LWJGL.window.getViewport();
        mapHandler = new MapHandler(this, clock);
        
        robotHandler = new RobotHandler(this);
        chooseHandler = new ObjectChooseHandler(this);
        
        if(!robotHandler.loadRobots(this)) {
            robotHandler.addRobot(new MinerRobot(this, new Vector2f(12.5f, 8.5f), "miner"), true);
            robotHandler.addRobot(new CraftingRobot(this, new Vector2f(7.5f, 5.5f), "crafter"), false);
            robotHandler.addRobot(new FarmingRobot(this, new Vector2f(7.5f, 10.5f), "farmer"), false);
        }
        robotHandler.getRobot("miner").getInventory().setModule(0, new SolarPanelModule(this, 0.2, 1));
        robotHandler.getRobot("crafter").getInventory().add(new MassItem(ItemTypes.COPPER_ORE, 5));
        
        gameUIHandler = new GameUIHandler(LWJGL.window, this);
        commandHandler = new CommandHandler(this);
        survivalHandler = new SurvivalHandler(100, 100, 100);
        
        gameHandler = new GameHandler(this);
        
    }

    @Override
    public void show() {
        
    }

    private boolean pause = false;
    
    @Override
    public void update(float delta) {
        if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            inputProcessor = gameUIHandler.clickedOn()? InputProcessor.GAME_UI:InputProcessor.GAME;
        }
        
        gameHandler.update(delta);
        gameUIHandler.tick(inputProcessor);
        robotHandler.update(inputProcessor);
        commandHandler.update(delta);
        survivalHandler.update(clock, delta);
        
        mapHandler.update();
        clock.update(delta);
        
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_ESCAPE)) {
            if(gameUIHandler.infoBarVisible()) {
                gameUIHandler.closeInfoBar();
            } else {
                game.setState(game.getOptionState());
            }
        }
        
    }

    @Override
    public void render() {
        if(gameFBO == null | recreateFBO) {
            resize(LWJGL.window.getWidth(), LWJGL.window.getHeight());
            recreateFBO = false;
        }
        GLCalls.setClearColor(new StaticColor(0.1f));
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GLCalls.enable(GL11.GL_BLEND, GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        gameFBO.bind();
        
        GLCalls.setClearColor(new StaticColor(0.1f));
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        
        windowViewport.set(LWJGL.window);
        
        gameHandler.render();
        
        LWJGL.graphics.begin();
        
        gameUIHandler.render(LWJGL.graphics);
        
        LWJGL.graphics.end();
        
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

    public ObjectChooseHandler getChooseHandler() {
        return chooseHandler;
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

    public SurvivalHandler getSurvivalHandler() {
        return survivalHandler;
    }
    
    public void resize(int width, int height) {
        LWJGL.graphics.updateSize(width, height);
        windowViewport = new Viewport(0, 0, width, height);
        if(gameFBO != null) gameFBO.dispose();
        gameFBO = new FBO(GL30.GL_FRAMEBUFFER);
        gameFBO.bind();
        
        System.out.println(width + " " + height);
        
        RBO rbo = new RBO(GL30.GL_DEPTH24_STENCIL8, width, height);
        gameFBO.attachRBO(GL30.GL_DEPTH_STENCIL_ATTACHMENT, rbo);
        gameTexture = gameFBO.createTexture();
        gameFBO.checkProgress(System.out, true);
        gameFBO.unbind();
        
    }

    public void resize() {
        recreateFBO = true;
    }

    public void chosen(ObjectChooseHandler.ChoosingObject choosing) {
        if(gameUIHandler == null)   return;
        ObjectChooseHandler.Choosable choosingObject = choosing.getObject();
        if(choosingObject instanceof MapCell) {
            gameUIHandler.showInfoBar(1);
        } else if(choosingObject instanceof Robot) {
            gameUIHandler.showInfoBar(0);
            CodingFX.currentController.addRobot((Robot) choosingObject);
        } else if(choosingObject instanceof EntityData) {
            EntityData data = (EntityData) choosingObject;
            if(data.isTileEntity()) {
                gameUIHandler.showInfoBar(1);
            }
        }
    }
    
}
