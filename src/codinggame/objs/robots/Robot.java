/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.robots;

import codinggame.globjs.Camera;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.objs.Battery;
import codinggame.objs.RobotInventory;
import codinggame.objs.modules.Module;
import codinggame.states.GameState;
import codinggame.states.InputProcessor;
import codinggame.ui.CodingArea;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.math.MathUtils;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;


/**
 *
 * @author Welcome
 */
public class Robot implements Serializable{
    public static final String NAME = "Robot";
    
    protected boolean running = false;
    
    protected transient GameState game;
    protected String name;
    protected Vector2f position;
    protected RobotInventory inventory;
    protected List<Module> modules;
    protected Battery battery;
    protected transient IColor color = IColor.LIME;
    protected transient boolean hovering;
    
    private transient Object lock = new Object();
    
    public Robot(GameState game, Vector2f position, String name) {
        this.game = game;
        this.name = name;
        this.position = position;
        this.modules = new ArrayList<>();
        inventory = new RobotInventory(30);
        battery = new Battery(50000, 100000);
    }
    
    public void update(InputProcessor inputProcessor) {
        Robot selectedRobot = game.getRobotHandler().getCurrentRobot();
        color = selectedRobot == this? IColor.GOLDENROD:IColor.LIME;
        hovering = isBeingHovered() && inputProcessor == InputProcessor.GAME;
        if(hovering) {
            if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                color = color.darker();
                game.getRobotHandler().selectRobot(this);
            } else {
                color = color.tint(0.5f);
            }
        }
        MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).getTileAt(getTileX(), getTileY());
        if(cell == null)    return;
        MapTile tile = cell.getTileType();
        if(tile == null)    return;
        if(tile.getID() == MapTile.CHARGER && Math.random() < LWJGL.currentLoop.getDeltaTime() * 5) {
            battery.increase(1);
        }
    }
    
    public void render(NVGGraphics g) {
        Camera camera = game.getCamera();
        Vector2f pixelPosition = getPixelPosition(camera);
        Vector2f translatedPosition = new Vector2f(pixelPosition).add(camera.getPixelTranslation());
        
        if(hovering) {
            int x = LWJGL.mouse.getCursorX();
            int y = LWJGL.mouse.getCursorY();
            int w = 250, h = 100;
            g.push();
            g.translate(MathUtils.clamp(0, x, game.getMapViewport().getWidth() - w), MathUtils.clamp(0, y, game.getMapViewport().getHeight() - h));
            g.rect(0, 0, w, h);
            g.fill(IColor.LIGHTGRAY);
            g.setUpText(CodingArea.textFont, IColor.WHITE, 20, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
            g.beginPath();
            g.translate(10, 10);
            g.text(getTypeName(), 0, 0);
            g.translate(0, 20);
            g.text("Name: " + name, 0, 0);
            g.translate(0, 20);
            g.text("Battery: " + (int) battery.getEnergy() + "/" + (int) battery.getMaxEnergyCapacity(), 0, 0);
            g.pop();
        }
        g.push();
        g.strokeWeight(2);
        g.circle(translatedPosition.x, translatedPosition.y, 8);
        g.fill(color);
        g.circle(translatedPosition.x, translatedPosition.y, 12);
        g.stroke(color);
        g.pop();
    }
    
    public void centerOnThis(Camera camera) {
        Viewport viewport = game.getMapViewport();
        Vector2f pixelPosition = getPixelPosition(camera).mul(-1)
                .add(viewport.getWidth() / 2, viewport.getHeight() / 2);
        camera.setPixelTranslation(pixelPosition);
    }
    
    public void move(Vector2f move) {
        position.add(move);
    }

    public int getTileY() {
        return (int) Math.floor(position.y);
    }

    public int getTileX() {
        return (int) Math.floor(position.x);
    }
    
    public Vector2f getPixelPosition(Camera camera) {
        Vector2f pixelPosition = new Vector2f(position).mul(camera.getTileSize());
        
        int viewportHeight = LWJGL.window.getViewport().getHeight();
        pixelPosition.y = viewportHeight - pixelPosition.y;
        return pixelPosition;
    }
    
    public void beginMoving() {
        running = true;
    }
    
    public void endMoving() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void moveTo(Vector2f destination) {
        this.position = new Vector2f(destination);
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

    public boolean isBeingHovered() {
        Vector2f pixelPosition = getPixelPosition(game.getCamera());
        Vector2f translatedPosition = new Vector2f(pixelPosition).add(game.getCamera().getPixelTranslation());
        return LWJGL.mouse.getCursorPosition().distanceSquared(translatedPosition) < 16 * 16;
    }

    public Object getLock() {
        return lock;
    }
    
    public void createLock() {
        lock = new Object();
    }
    
    
}
