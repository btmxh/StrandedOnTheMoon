/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.CodingGame;
import codinggame.map.GameMap;
import codinggame.map.proceduralmap.chunkloading.ChunkIO;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.cells.DataCell;
import codinggame.map.proceduralmap.chunkloading.ChunkLocation;
import codinggame.map.proceduralmap.chunkloading.ChunkThread;
import codinggame.map.structures.CrashSpaceshipStructure;
import com.lwjglwrapper.LWJGL;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

/**
 *
 * @author Welcome
 */
public class ProcMapLayer implements MapLayer{
    
    private final ProcMap map;
    private int id;
    
    private Map<Point, ProcMapChunk> inMemory = new HashMap<>();

    public ProcMapLayer(ProcMap map, int id) {
        this.map = map;
        this.id = id;
        
        LWJGL.window.getWindowCallbacks().getWindowCloseCallback().add(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                for (Point coords : inMemory.keySet()) {
                    ProcMapChunk chunk = inMemory.get(coords);
                    try {
                        ChunkIO.writeChunk(map.getChunkDirectory(id, coords.x, coords.y), chunk);
                        String image = map.getChunkDirectory(id, coords.x, coords.y) + ".png";
                    } catch (IOException ex) {
                        Logger.getLogger(ProcMapLayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    @Override
    public void setTileAt(int x, int y, MapTile tile) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
        chunk.setTileAt(tileX, tileY, tile);
    }

    @Override
    public void setTileAt(int x, int y, MapCell cell) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
        if(cell instanceof DataCell) {
            chunk.putDataCell(x, y, (DataCell) cell);
        }
        chunk.setTileAt(tileX, tileY, (ProcMapCell) cell);
    }

    @Override
    public MapCell getTileAt(int x, int y) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return null;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
        return chunk.getTileAt(tileX, tileY);
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    public ProcMap getMap() {
        return map;
    }
    
    public ProcMapChunk getChunk(int chunkX, int chunkY) {
        ProcMapChunk chunk = inMemory.get(new Point(chunkX, chunkY));
        if(chunk != null) {
            chunk.lastFrameUsed = getFrameID();
            return chunk;
        }
        String chunkPath = map.getChunkDirectory(id, chunkX, chunkY);
        if(!new File(chunkPath).exists()) {
            chunk = map.getChunkGenerator().generate(map, id, chunkX, chunkY);
            inMemory.put(new Point(chunkX, chunkY), chunk);
            chunk.lastFrameUsed = getFrameID();
            generateStructures(chunk, chunkX, chunkY);
            return chunk;
        }
        try {
            chunk = ChunkIO.loadChunk(map.getChunkDirectory(id, chunkX, chunkY), map.getTileset());
            inMemory.put(new Point(chunkX, chunkY), chunk);
            return chunk;
//            ChunkThread.registerLoadChunk(new ChunkLocation.Load(this, chunkX, chunkY) {
//                @Override
//                public void afterLoading(ProcMapChunk chunk) {
//                    inMemory.put(new Point(chunkX, chunkY), chunk);
//                }
//                
//                @Override
//                public void loadFailed() {
//                    System.out.println("Failed");
//                }
//            });
//            return null;
        } catch (Exception ex) {
            Logger.getLogger(ProcMapLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chunk;
    }
    
    public void update() {
        long frameID = ((CodingGame) LWJGL.currentLoop).getFrameID();
        for (Iterator<Point> it = inMemory.keySet().iterator(); it.hasNext();) {
            Point pos = it.next();
            ProcMapChunk chunk = inMemory.get(pos);
            
            if(chunk.lastFrameUsed != frameID) {
//                ChunkThread.registerSaveChunk(new ChunkLocation.Save(this, pos.x, pos.y) {
//                    @Override
//                    public void afterSaving(ProcMapChunk chunk) {
//                        System.out.println("Saved");
//                    }
//                    
//                    @Override
//                    public void saveFailed() {
//                        System.err.println("Failed");
//                    }
//                }, chunk);
                new Thread(() -> {
                    try {
                        ChunkIO.writeChunk(map.getChunkDirectory(id, pos.x, pos.y), chunk);
                    } catch (IOException ex) {
                        Logger.getLogger(ProcMapLayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
                System.gc();
                it.remove();
            }
        }
        
    }
    
    private long getFrameID() {
        return ((CodingGame) LWJGL.currentLoop).getFrameID();
    }

    private void generateStructures(ProcMapChunk chunk, int chunkX, int chunkY) {
        System.out.println(id);
        if(chunkX == 0 && id == GameMap.ORE_LAYER && chunkY == 0) {
            new CrashSpaceshipStructure(new Vector2i(5, 5), map.getTilesets()).set(this);
        }
    }
}
