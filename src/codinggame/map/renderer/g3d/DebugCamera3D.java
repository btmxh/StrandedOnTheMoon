/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Mouse;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.utils.input.KeyBindings;
import com.lwjglwrapper.utils.math.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public abstract class DebugCamera3D extends Camera3D{


    private float hspeed = 5, vspeed = 5, rspeed = 5;

    public DebugCamera3D(Vector3f rotatePoint, float fov, Window window,
            float zNear, float zFar) {
        super(rotatePoint, fov, window, zNear, zFar);
    }
    
    public void setSpeed(float hspeed, float vspeed, float rspeed) {
        this.hspeed = hspeed;
        this.vspeed = vspeed;
        this.rspeed = rspeed;
    }

    @Override
    public void move(KeyBindings keys, float delta) {
        moveHorizontal(keys, delta);
        moveVertical(keys, delta);
        calculateRotation(LWJGL.mouse);
    }

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
        
        position.add(vel.x, 0, vel.y);
    }

    private void moveVertical(KeyBindings keys, float delta) {
        if(keys.anyDown("up")) {
            position.add(0, vspeed * delta, 0);
        } else if(keys.anyDown("down")) {
            position.add(0, -vspeed * delta, 0);
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
