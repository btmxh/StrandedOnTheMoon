/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

import codinggame.handlers.ObjectChooseHandler;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 *
 * @author Welcome
 */
public class EntityData implements ObjectChooseHandler.Choosable{

    public static final int POSITION_2D = 0,
                            TYPE = POSITION_2D + Integer.BYTES * 2,
                            TILE_ID = TYPE + 1,
                            TAG = TILE_ID + Integer.BYTES,
                            HEIGHT = TAG + Integer.BYTES,
                            STRIDE = HEIGHT + Integer.BYTES;
    
    public static final byte TILE_ENTITY = 0,
                             SPACESHIP = 1;

    public static EntityData empty() {
        return new EntityData(ByteBuffer.allocate(STRIDE));
    }
    
    private ByteBuffer buffer;

    public EntityData(ByteBuffer buffer) {
        if(buffer.capacity() < STRIDE) {
            ByteBuffer newBuffer = ByteBuffer.allocate(STRIDE);
            newBuffer.put(buffer);
            newBuffer.position(0);
            buffer = newBuffer;
        }
        this.buffer = buffer;
    }

    @Override
    public String toString() {
//        return Arrays.toString(buffer.array());
        return getTileID() + "";
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public boolean isTileEntity() {
        buffer.position(TYPE);
        byte type = buffer.get();
        return type == TILE_ENTITY;
    }
    
    public int getTileID() {
        buffer.position(TILE_ID);
        int tileID = buffer.getInt();
        return tileID;
    }
    
    public int getTagID() {
        buffer.position(TAG);
        int tileID = buffer.getInt();
        return tileID;
    }
    
    public void setTagID(int tag) {
        buffer.position(TAG);
        buffer.putInt(tag);
    }

    public Vector2i getPosition() {
        buffer.position(POSITION_2D);
        Vector2i pos = BufferSerializer.getVec2i(buffer);
        return pos;
    }
    
    public float getHeight() {
        buffer.position(HEIGHT);
        return buffer.getFloat();
    }
    
    public void setHeight(float height) {
        buffer.position(HEIGHT);
        buffer.putFloat(height);
    }

    public Vector3f get3DPosition() {
        Vector2i xz = getPosition();
        float y = getHeight();
        return new Vector3f(xz.x, y, xz.y);
    }

    public void setPosition(Vector2f xz) {
        buffer.position(POSITION_2D);
        buffer.putInt((int) xz.x).putInt((int) xz.y);
    }
}
