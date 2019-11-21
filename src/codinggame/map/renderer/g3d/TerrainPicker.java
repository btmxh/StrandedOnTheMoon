/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d;

import codinggame.map.renderer.g3d.terrain.RenderableChunk;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.utils.MousePicker;
import com.lwjglwrapper.utils.cameras.Camera;
import java.text.DecimalFormat;
import org.joml.Rayf;
import org.joml.Vector3f;

/**
 *
 * @author Welcome
 */
public abstract class TerrainPicker extends MousePicker{
    
    public static final int RECURSION_COUNT = 200, RAY_RANGE = 100;
    public Vector3f currentTerrainPoint;
    public float intersectionRayT;  //let ray's equation be M = M0 + dir * t, intersectionRayT is the t value of the intersection point
    public Vector3f ray;
    
    public TerrainPicker(Window window) {
        super(window);
    }

    public void update(Camera camera) {
        ray = calculateMouseRay(camera);
        if (intersectionInRange(camera.getPosition(), 0, RAY_RANGE, ray)) {
            currentTerrainPoint = binarySearch(camera.getPosition(), 0, 0, RAY_RANGE, ray);
        } else {
            currentTerrainPoint = null;
        }
    }

    private Vector3f binarySearch(Vector3f origin, int count, float start, float finish, Vector3f ray) {
        intersectionRayT = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = pointOnRay(origin, ray, intersectionRayT);
            return endPoint;
        }
        if (intersectionInRange(origin, start, intersectionRayT, ray)) {
            return binarySearch(origin, count + 1, start, intersectionRayT, ray);
        } else {
            return binarySearch(origin, count + 1, intersectionRayT, finish, ray);
        }
    }

    public Vector3f pointOnRay(Vector3f origin, Vector3f ray, float dist) {
        return origin.add(ray.mul(dist, new Vector3f()), new Vector3f());
    }

    private boolean intersectionInRange(Vector3f origin, float start, float finish, Vector3f ray) {
        Vector3f startPoint = pointOnRay(origin, ray, start);
        Vector3f endPoint = pointOnRay(origin, ray, finish);
        return !isUnderGround(startPoint) && isUnderGround(endPoint);
    }
    
    private boolean isUnderGround(Vector3f point) {
        float height = height(point.x, point.z);
        return height >= point.y;
    }
    
    public abstract float height(float x, float z);

    public Rayf getRay(Camera camera) {
        return new Rayf(camera.getPosition(), ray);
    }
}
