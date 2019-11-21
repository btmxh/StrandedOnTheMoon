/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.MapCell;
import codinggame.map.tiles.MapTiles;
import codinggame.utils.ByteArray;
import com.lwjglwrapper.utils.math.MathUtils;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Welcome
 */
public class ProcMapCell implements MapCell, Serializable{

    public static final int TILE_ID = 0, TILE_TYPE = Integer.BYTES;

    public static ProcMapCell createCell(int tileID) {
        byte[] arr = new byte[Integer.BYTES];
        ByteArray.putInt(arr, TILE_ID, tileID);
        return new ProcMapCell(arr, Integer.BYTES);
    }
    
    protected byte[] data;
    protected int stride;

    public ProcMapCell(byte[] data, int stride) {
        this.data = data;
        this.stride = stride;
    }
    
    @Override
    public int getBreakingStage() {
        if(!isBreaking())    return -1;
        else return MathUtils.clamp(0, (int) (0 * 4), 3);
    }

    @Override
    public void increaseBreakingProgress(float deltabp) {
    }

    @Override
    public boolean isBreaking() {
        return false;
    }

    @Override
    public int getTileID() {
        return ByteArray.getInt(data, TILE_ID);
    }

    public void write(FileChannel channel) throws IOException {
        ByteBuffer strideBuffer = ByteBuffer.allocate(Integer.BYTES);
        strideBuffer.putInt(stride);
        strideBuffer.flip();
        channel.write(strideBuffer);
        channel.write(ByteArray.toBuffer(data));
    }

    @Override
    public String toString() {
        return MapTiles.get(getTileID()).getName();
    }
    
}
