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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    private static final int CHUNK_LOAD_DISTANCE = 8;
    private static final int SQ_CLD = CHUNK_LOAD_DISTANCE * CHUNK_LOAD_DISTANCE;
    private final ProcMap map;
    private int id;
    private float cameraChunkX, cameraChunkY;
    
    private final Map<Point, ProcMapChunk> inMemory = Collections.synchronizedMap(new HashMap<>());

    public ProcMapLayer(ProcMap map, int id) {
        this.map = map;
        this.id = id;
        
        LWJGL.window.getWindowCallbacks().getWindowCloseCallback().add(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                ChunkThread.stopThread();
                for (Point coords : inMemory.keySet()) {
                    ProcMapChunk chunk = inMemory.get(coords);
                    try {
                        ChunkIO.writeChunk(map.getChunkDirectory(id, coords.x, coords.y), chunk);
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
        if((chunkX - cameraChunkX) * (chunkX - cameraChunkX) + (chunkY - cameraChunkY) * (chunkY - cameraChunkY) > SQ_CLD) {
            return null;
        }
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
        }
        return chunk;
    }
    
    public void update(int cameraChunkX, int cameraChunkY) {
        this.cameraChunkX = cameraChunkX;
        this.cameraChunkY = cameraChunkY;
        List<Point> inRadius = new ArrayList<>();
        synchronized (inMemory) {
            for (int dx = -CHUNK_LOAD_DISTANCE; dx <= CHUNK_LOAD_DISTANCE; dx++) {
                for (int dy = -CHUNK_LOAD_DISTANCE; dy <= CHUNK_LOAD_DISTANCE; dy++) {
                    if(dx * dx + dy * dy > SQ_CLD)  continue;
                    inRadius.add(new Point(dx + cameraChunkX, dy + cameraChunkY));
                    if(!inMemory.containsKey(new Point(dx + cameraChunkX, dy + cameraChunkY))) {
                        ChunkThread.registerLoadChunk(new ChunkLocation.Load(this, dx + cameraChunkX, dy + cameraChunkY) {
                            @Override
                            public void afterLoading(ProcMapChunk chunk) {
                                synchronized(inMemory) {
                                    inMemory.put(new Point(chunkX, chunkY), chunk);
                                }
                            }

                            @Override
                            public void loadFailed() {
                                System.out.println("Failed");
                            }
                        });
                    }
                }
            }
        
            for (Iterator<Point> it = inMemory.keySet().iterator(); it.hasNext();) {
                Point pos = it.next();
                ProcMapChunk chunk = inMemory.get(pos);

                if(!inRadius.contains(pos)) {
                    ChunkThread.registerSaveChunk(new ChunkLocation.Save(this, pos.x, pos.y) {
                        @Override
                        public void afterSaving(ProcMapChunk chunk) {
                            System.out.println("Saved chunk " + pos);
                        }

                        @Override
                        public void saveFailed() {
                            System.err.println("Failed");
                        }
                    }, chunk);
                    it.remove();
    //                new Thread(() -> {
    //                    try {
    //                        ChunkIO.writeChunk(map.getChunkDirectory(id, pos.x, pos.y), chunk);
    //                    } catch (IOException ex) {
    //                        Logger.getLogger(ProcMapLayer.class.getName()).log(Level.SEVERE, null, ex);
    //                    }
    //                }).start();
                }
            }
        }
        
    }
    
    private long getFrameID() {
        return ((CodingGame) LWJGL.currentLoop).getFrameID();
    }

    private void generateStructures(ProcMapChunk chunk, int chunkX, int chunkY) {
        if(chunkX == 0 && id == GameMap.ORE_LAYER && chunkY == 0) {
            new CrashSpaceshipStructure(new Vector2i(5, 5), map.getTilesets()).set(this);
        }
    }
}
