/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Mouse;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.utils.cameras.Camera;
import com.lwjglwrapper.utils.input.KeyBindings;
import com.lwjglwrapper.utils.math.FloatMath;
import com.lwjglwrapper.utils.math.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

/**
 *
 * @author Welcome
 */
public abstract class Camera3D extends Camera{

    private float dist = 1f;
    private float angle;
    private Vector3f rotatePoint;
    private float hspeed = 5, vspeed = 5, rspeed = 5;
    
    public Camera3D(Vector3f rotatePoint, float fov, Window window, float zNear,
            float zFar) {
        super(fov, window, zNear, zFar);
        this.rotatePoint = rotatePoint;
        LWJGL.mouse.getScrollCallback().add(new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scroll(yoffset);
            }
        });
    }

    @Override
    public void move(KeyBindings bindings, float delta) {
        
        float horDist = dist * FloatMath.cosDeg(-rotation.x);
        float yOff = -dist * FloatMath.sinDeg(-rotation.x);
        
        float theta = angle;
        float xOff = -horDist * FloatMath.cosDeg(theta);
        float zOff = -horDist * FloatMath.sinDeg(theta);
        
        if(LWJGL.mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            float dx = LWJGL.mouse.getDeltaX() * 5;
            angle += dx;
            
            float dy = LWJGL.mouse.getDeltaY() * 5;
            rotation.x += dy;
            rotation.x = MathUtils.clamp(0.0f, rotation.x, 90.0f);
        } else if(LWJGL.mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            rotatePoint.x += LWJGL.mouse.getDeltaY() * FloatMath.cosDeg(theta);
            rotatePoint.z += LWJGL.mouse.getDeltaY() * FloatMath.sinDeg(theta);
            theta += 90;
            rotatePoint.x += LWJGL.mouse.getDeltaX() * FloatMath.cosDeg(theta);
            rotatePoint.z -= LWJGL.mouse.getDeltaX() * FloatMath.sinDeg(theta);
            theta -= 90;
            //rotatePoint.z += LWJGL.mouse.getDeltaY();
        }
        
        
        position = rotatePoint.add(xOff, yOff, zOff, new Vector3f());
        position.y = Math.max(getWorldHeight(position.x, position.z) + 0.5f, position.y);
        
        rotation.y = 90 + theta;
        
//        calculateRotation(LWJGL.mouse);
//        moveHorizontal(bindings, delta);
//        moveVertical(bindings, delta);
    }
    
    private void scroll(double yoffset) {
        dist -= yoffset;
        //dist = MathUtils.clamp(minDist, dist, dist + 1);
    }

    public Vector3f getRotatePoint() {
        return rotatePoint;
    }
    
    public abstract float getWorldHeight(float x, float z);
    
    private void moveHorizontal(KeyBindings keys, float delta) {
        Vector2f vel = new Vector2f();
        if (keys.anyDown("forward")) {
            vel.add(-1, 0);
        }
        if (keys.anyDown("backward")) {
            vel.add(1, 0);
        }
        if (keys.anyDown("left")) {
            vel.add(0, 1);
        }
        if (keys.anyDown("right")) {
            vel.add(0, -1);
        }
        if (vel.equals(0, 0)) {
            return;
        }
        
        vel = MathUtils.rotate(vel, 90 + rotation.y).normalize(hspeed * delta);
        
        rotatePoint.add(vel.x, 0, vel.y);
    }

    private void moveVertical(KeyBindings keys, float delta) {
        if(keys.anyDown("up")) {
            rotatePoint.add(0, vspeed * delta, 0);
        } else if(keys.anyDown("down")) {
            rotatePoint.add(0, -vspeed * delta, 0);
        }
    }
    private void calculateRotation(Mouse mouse) {
        if(mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            rotation.x += mouse.getDeltaY() * rspeed;
            rotation.y -= -mouse.getDeltaX() * rspeed;
            
            rotation.x = MathUtils.clamp(-89.9f, rotation.x, 89.9f);
        }
    }
    
}
