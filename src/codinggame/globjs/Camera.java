/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.globjs;

import codinggame.CodingGame;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.states.InputProcessor;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.utils.math.MathUtils;
import java.awt.Point;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

/**
 *
 * @author Welcome
 */
public class Camera {

    private Vector2f pixelTranslation;
    private float tileSize = 32;

    public Camera() {
        pixelTranslation = new Vector2f();
        LWJGL.mouse.getScrollCallback().add(new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                if (CodingGame.getInstance().gs.getInputProcessor() != InputProcessor.GAME) {
                    return;
                }
                tileSize = MathUtils.clamp(4f, tileSize + (float) yoffset, 256f);
            }
        });
    }

    public void update(boolean focus) {
        if (!focus) {
            return;
        }
        if (LWJGL.mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            float dx = LWJGL.mouse.getDeltaX() * 32;
            float dy = LWJGL.mouse.getDeltaY() * 32;

            pixelTranslation.add(dx, dy);
        }

    }

    /**
     * @return the translation
     */
    public Vector2f getPixelTranslation() {
        return new Vector2f(pixelTranslation);
    }

    public Vector2f getGLTranslation(Viewport viewport) {
        return new Vector2f(pixelTranslation).mul(2f / viewport.getWidth(), 2f / viewport.getHeight());
    }

    /**
     * @param pixelTranslation the translation to set
     */
    public void setPixelTranslation(Vector2f pixelTranslation) {
        this.pixelTranslation = pixelTranslation;
    }

    public float getTileSize() {
        return tileSize;
    }

    public Point getChunk() {
        Vector2f centerTile = getPixelTranslation().negate().add(LWJGL.window.getWidth() / 2, 0).mul(1 / tileSize);
        int chunkX = (int) Math.floor(centerTile.x / ProcMapChunk.CHUNK_SIZE);
        int chunkY = (int) -Math.floor(centerTile.y / ProcMapChunk.CHUNK_SIZE);
        return new Point(chunkX, chunkY);
    }

}
