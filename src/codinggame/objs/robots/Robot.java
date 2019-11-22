/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.robots;

import codinggame.globjs.Camera;
import codinggame.handlers.ObjectChooseHandler;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.entities.rendering.ModelManager;
import codinggame.map.renderer.g3d.entities.Entity;
import codinggame.objs.Battery;
import codinggame.objs.RobotInventory;
import codinggame.objs.modules.Module;
import codinggame.objs.modules.SolarPanelModule;
import codinggame.objs.modules.SpeedModule;
import codinggame.states.GameState;
import codinggame.states.InputProcessor;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.joml.Vector2f;


/**
 *
 * @author Welcome
 */
public class Robot extends Entity implements Serializable, ObjectChooseHandler.Choosable{
    public static final String NAME = "Robot";
    
    protected boolean running = false;
    
    protected transient GameState game;
    protected String name;
    protected RobotInventory inventory;
    protected Battery battery;
    protected transient boolean hovering;
    protected transient boolean stop = false;
    protected String code;
    
    private transient Object lock = new Object();
    
    public Robot(GameState game, Vector2f position, String name) {
        super(ModelManager.ROBOT);
        this.game = game;
        this.name = name;
        this.position.set(position.x, 0, position.y);
        inventory = new RobotInventory(30);
        battery = new Battery(50000, 100000);
        code = sampleCode();
    }
    
    public void update(InputProcessor inputProcessor) {
        for (Module module : inventory.getModules()) {
            if(module instanceof SolarPanelModule) {
                ((SolarPanelModule) module).update(this, (float) LWJGL.currentLoop.getDeltaTime());
            }
        }
        MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER).getTileAt(getTileX(), getTileY());
        if(!MapCell.nullCheck(cell))    return;
        int tileID = cell.getTileID();
        if(tileID == MapTile.CHARGER && Math.random() < LWJGL.currentLoop.getDeltaTime() * 5) {
            battery.increase(1);
        }
    }

    @Override
    public void render() {
        super.render();
    }
    
    public void centerOnThis(Camera camera) {
        Viewport viewport = game.getMapViewport();
        Vector2f pixelPosition = getPixelPosition(camera).mul(-1)
                .add(viewport.getWidth() / 2, viewport.getHeight() / 2);
        camera.setPixelTranslation(pixelPosition);
    }
    
    public void move(Vector2f move) {
        position.add(move.x, 0, move.y);
    }

    public int getTileY() {
        return (int) Math.floor(position.z);
    }

    public int getTileX() {
        return (int) Math.floor(position.x);
    }
    
    public Vector2f getPixelPosition(Camera camera) {
        Vector2f pixelPosition = new Vector2f(position.x, position.z).mul(camera.getTileSize());
        
        int viewportHeight = LWJGL.window.getViewport().getHeight();
        pixelPosition.y = viewportHeight - pixelPosition.y;
        return pixelPosition;
    }
    
    public void beginMoving() {
        running = true;
        stop = false;
    }
    
    public void endMoving() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }

    public Vector2f get2DPosition() {
        return new Vector2f(position.x, position.z);
    }

    public void moveTo(Vector2f destination) {
        this.position.x = destination.x;
        this.position.z = destination.y;
    }

    public RobotInventory getInventory() {
        return inventory;
    }
    
    public Battery getBattery() {
        return battery;
    }

    public boolean instanceOf(Class<? extends Robot> cl) {
        return cl.isInstance(this);
    }

    public String getName() {
        return name;
    }
    
    public void setGameState(GameState game) {
        this.game = game;
    }
    
    public String getTypeName() {
        try {
            return (String) getClass().getField("NAME").get(null);
        } catch (Exception ex) {
            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public Object getLock() {
        return lock;
    }
    
    public void createLock() {
        lock = new Object();
    }

    public boolean stopped() {
        return stop;
    }
    
    public void stopCommands() {
        stop = true;
    }

    public float getSpeed() {
        return (float) Stream.of(inventory.getModules())
                .filter(SpeedModule.class::isInstance)
                .map(SpeedModule.class::cast)
                .mapToDouble(SpeedModule::getScale)
                .reduce(1, (a, b) -> a*b);
    }

    public void setSourceCode(String code) {
        this.code = code;
    }

    public String getSourceCode() {
        return code;
    }

    private static String sampleCode() {
        return "public void main() {\n\t\n}";
    }

    public void select(float t) {
        game.getRobotHandler().selectRobot(this, t);
    }
    
    
}
