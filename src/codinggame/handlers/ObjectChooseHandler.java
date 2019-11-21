/*
 * To change this license header, mouseReleased License Headers in Project Properties.
 * To change this template file, mouseReleased Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.map.MapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.renderer.g3d.TerrainPicker;
import codinggame.map.renderer.g3d.entities.AABBUtils;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.Utils;
import com.lwjglwrapper.LWJGL;
import java.util.Objects;
import org.joml.AABBf;
import org.joml.Rayf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public class ObjectChooseHandler {
    
    public static final ChoosingObject NULL = new ChoosingObject(0, 0, null, null);
    
    private final GameState game;
    private Rayf mouseRay;
    
    private ChoosingObject choosing = NULL;
    private ChoosingObject frameChoosing = NULL;
    private ChoosingObject frame_tempChoosing = NULL;
    private float pressRecord;
    private float record;

    public ObjectChooseHandler(GameState game) {
        this.game = game;
    }
    
    public void mousePressed(ChoosingObject choosing, float t) {
        Objects.requireNonNull(choosing);
        if(choosing == NULL) throw new RuntimeException("choosing != NULL");
        if(t < pressRecord) {
            this.pressRecord = t;
            this.frame_tempChoosing = choosing;
        }
    }
    
    public void mouseReleased(ChoosingObject choosing, float t) {
        Objects.requireNonNull(choosing);
        if(choosing == NULL) throw new RuntimeException("choosing != NULL");
        if(choosing.equals(frame_tempChoosing)) {
            this.record = t;
            this.frameChoosing = choosing;
        }
    }
    
    public void unchoose() {
        choosing = NULL;
        frameChoosing = NULL;
        frame_tempChoosing = NULL;
    }
    
    public void beginFrame(Rayf mouseRay) {
        frameChoosing = NULL;
        record = Float.POSITIVE_INFINITY;
        pressRecord = Float.POSITIVE_INFINITY;
        this.mouseRay = new Rayf(mouseRay);
    }
    
    public void endFrame() {
        if(frameChoosing != NULL) {
            choosing = frameChoosing;
            game.chosen(choosing);
        }
    }
    
    public void test(AABBf boundBox, Vector3f translation, ChoosingObject object) {
        AABBf translatedBoundBox = AABBUtils.translate(boundBox, translation);
        Vector2f result = new Vector2f();
        boolean test = translatedBoundBox.intersectRay(mouseRay, result);
        if(!test)   return;
        float nearPoint = result.x;
        if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))   mousePressed(object, nearPoint);
        else if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT))  mouseReleased(object, nearPoint);
    }
    
    public ChoosingObject getChoosingObject() {
        return choosing;
    }
    
    public static class ChoosingObject {
        private Vary<Integer> x, y;
        private Choosable choosing;
        private Object origin;

        public ChoosingObject(Vary<Integer> x, Vary<Integer> y,
                Choosable choosing, Object origin) {
            this.x = x;
            this.y = y;
            this.choosing = choosing;
            this.origin = origin;
        }
        
        public ChoosingObject(int x, int y, Choosable choosing, Object origin) {
            this(() -> x, () -> y, choosing, origin);
        }
        
        public <T extends Choosable> T getObject() {
            return (T) choosing;
        }
        
        public <T extends Choosable> T getObject(Class<T> clazz) {
            return (T) choosing;
        }

        public Object getOrigin() {
            return origin;
        }

        public int getX() {
            return x.get();
        }

        public int getY() {
            return y.get();
        }
        
        public static ChoosingObject robot(Robot robot) {
            return new ChoosingObject(() -> robot.getTileX(), () -> robot.getTileY(), robot, null);
        }
        
        public static ChoosingObject tile(int x, int y, MapLayer origin) {
            return new ChoosingObject(x, y, origin.getTileAt(x, y), origin);
        }
        
        public static ChoosingObject tileEntity(EntityData data, MapLayer origin) {
            return new ChoosingObject(data.getPosition().x, data.getPosition().y, data, origin);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ChoosingObject) {
                ChoosingObject that = (ChoosingObject) obj;
                return Utils.equals(this, that, ChoosingObject::getX, ChoosingObject::getY, ChoosingObject::getObject, ChoosingObject::getOrigin);
            }
            return false;
        }
        
        
    }
    
    public static interface Vary<T> {
        public T get();
    }
    
    public static interface Choosable {}
}
