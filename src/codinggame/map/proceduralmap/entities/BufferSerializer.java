/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

import java.awt.Point;
import java.nio.ByteBuffer;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 *
 * @author Welcome
 */
public class BufferSerializer {
    
    public static float[] getFloats(ByteBuffer buffer, int num) {
        float[] arr = new float[num];
        buffer.asFloatBuffer().get(arr);
        buffer.position(buffer.position() + num * Float.BYTES);
        return arr;
    }
    
    public static int[] getInts(ByteBuffer buffer, int num) {
        int[] arr = new int[num];
        buffer.asIntBuffer().get(arr);
        buffer.position(buffer.position() + num * Integer.BYTES);
        return arr;
    }
    
    public static Vector2f getVec2f(ByteBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        return new Vector2f(x, y);
    }
    
    public static Vector3f getVec3f(ByteBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();
        return new Vector3f(x, y, z);
    }
    
    public static Vector2i getVec2i(ByteBuffer buffer) {
        int x = buffer.getInt();
        int y = buffer.getInt();
        return new Vector2i(x, y);
    }
    
    public static Vector3i getVec3i(ByteBuffer buffer) {
        int x = buffer.getInt();
        int y = buffer.getInt();
        int z = buffer.getInt();
        return new Vector3i(x, y, z);
    }

    public static Point getAWTPoint(ByteBuffer buffer) {
        int x = buffer.getInt();
        int y = buffer.getInt();
        return new Point(x, y);
    }
    
}
