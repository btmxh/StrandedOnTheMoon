/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.chunkloading;

import codinggame.map.MapTile;
import codinggame.map.MapTileset;
import codinggame.map.MapTilesets;
import codinggame.map.cells.DataCell;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class ChunkIO {
    
    public static ProcMapChunk loadChunk(String path, MapTileset tileset) throws IOException {
        File chunkFile = new File(path);
        File chunkDataFile = new File(path + "d");
        if(chunkFile.exists()) {
            DataInputStream dis = new DataInputStream(new FileInputStream(chunkFile));
            int length = ProcMapChunk.CHUNK_SIZE * ProcMapChunk.CHUNK_SIZE;
            ProcMapChunk chunk = new ProcMapChunk();
            for (int i = 0; i < length; i++) {
                int y = i / ProcMapChunk.CHUNK_SIZE;
                int x = i % ProcMapChunk.CHUNK_SIZE;
                chunk.setTileAt(x, y, tileset.getTile(dis.readInt()));
            }
            dis.close();
            
            if(chunkDataFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chunkDataFile));
                try {
                    Object obj = ois.readObject();
                    if(obj != null) {
                        chunk.setDataCells((Map<Point, DataCell>) obj, new MapTilesets(new MapTileset[]{tileset}));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ChunkIO.class.getName()).log(Level.SEVERE, null, ex);
                }
                ois.close();
            }
            return chunk;
        } else {
            return null;
        }
    }
    
    public static void writeChunk(String path, ProcMapChunk chunk) throws IOException {
        if(chunk == null)   return;
        File chunkFile = new File(path);
        File chunkDataFile = new File(path + "d");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(chunkFile));
        for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
            for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
                ProcMapCell tile = chunk.getTileAt(x, y);
                if(tile == null? true:tile.getTileType() == null) {
                    dos.writeInt(0);
                } else dos.writeInt(chunk.getTileAt(x, y).getTileType().getID());
            }
        }
        dos.flush();
        dos.close();
        
        if(chunk.getDataCells().isEmpty())  return;
        ObjectOutputStream dataOutputStream = new ObjectOutputStream(new FileOutputStream(chunkDataFile));
        dataOutputStream.writeObject(chunk.getDataCells());
        dataOutputStream.close();
    }
    
}
