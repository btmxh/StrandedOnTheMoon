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
import codinggame.map.proceduralmap.chunkloading.ChunkLocation;
import codinggame.map.proceduralmap.chunkloading.ChunkThread;
import codinggame.map.proceduralmap.entities.EntityData;
import com.lwjglwrapper.LWJGL;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

/**
 *
 * @author Welcome
 */
public class ProcMapLayer implements MapLayer{
    private static final int CHUNK_LOAD_DISTANCE = 4;
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
                synchronized (inMemory) {
                    for (Point coords : inMemory.keySet()) {
                        ProcMapChunk chunk = inMemory.get(coords);
                        try {
                            ChunkIO.writeChunk(map.getChunkDirectory(id, coords.x, coords.y), chunk);
                        } catch (IOException ex) {
                            Logger.getLogger(ProcMapLayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setTileAt(int x, int y, int tileID) {
        setTileAt(x, y, ProcMapCell.createCell(tileID));
    }

    @Override
    public void setTileAt(int x, int y, MapCell cell) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
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
    
    public EntityData getEntityAt(int x, int y) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return null;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
        return chunk.getEntityAt(tileX, tileY);
    }

    public void setEntityAt(int x, int y, EntityData data) {
        ProcMapChunk chunk = getChunk((int) Math.floor((double) x / ProcMapChunk.CHUNK_SIZE), (int) Math.floor((double) y / ProcMapChunk.CHUNK_SIZE));
        if(chunk == null)   return;
        int tileX = x % ProcMapChunk.CHUNK_SIZE;
        if(tileX < 0)   tileX += ProcMapChunk.CHUNK_SIZE;
        int tileY = y % ProcMapChunk.CHUNK_SIZE;
        if(tileY < 0)   tileY += ProcMapChunk.CHUNK_SIZE;
        chunk.setEntityAt(tileX, tileY, data);
    }
    
    public void setTileEntityAt(int x, int y, int tileID, EntityData data) {
        if(tileID == -1) {
            setEntityAt(x, y, null);
            return;
        }
        if(data == null) {
            data = EntityData.empty();
        }
        ByteBuffer buffer = data.getBuffer();
        buffer.position(EntityData.POSITION_2D);
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.putInt(tileID);
        buffer.position(0);
        setEntityAt(x, y, data);
    }
    
    public void setTileEntityAt(int x, int y, MapCell cell, EntityData data) {
        if(MapCell.nullCheck(cell)) {
            setEntityAt(x, y, null);
            return;
        }
        setTileEntityAt(x, y, cell.getTileID(), data);
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
    
    public void getChunk(int chunkX, int chunkY, Consumer<ProcMapChunk> consumer) {
        ProcMapChunk chunk = inMemory.get(new Point(chunkX, chunkY));
        if(chunk != null) {
            chunk.lastFrameUsed = getFrameID();
            consumer.accept(chunk);
        }
        String chunkPath = map.getChunkDirectory(id, chunkX, chunkY);
        if(!new File(chunkPath).exists()) {
            chunk = map.getChunkGenerator().generate(map, id, chunkX, chunkY);
            inMemory.put(new Point(chunkX, chunkY), chunk);
            chunk.lastFrameUsed = getFrameID();
            generateStructures(chunk, chunkX, chunkY);
            return;
        }
//        ChunkThread.registerLoadChunk(new ChunkLocation.Load(this, chunkX, chunkY) {
//            @Override
//            public void afterLoading(ProcMapChunk chunk) {
//                consumer.accept(chunk);
//            }
//            
//            @Override
//            public void loadFailed() {
//            }
//        });
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
//                    ChunkThread.registerSaveChunk(new ChunkLocation.Save(this, pos.x, pos.y) {
//                        @Override
//                        public void afterSaving(ProcMapChunk chunk) {
//                            System.out.println("Saved chunk " + pos);
//                        }
//
//                        @Override
//                        public void saveFailed() {
//                            System.err.println("Failed");
//                        }
//                    }, chunk);
//                    it.remove();
                }
            }
        }
        
    }
    
    private long getFrameID() {
        return ((CodingGame) LWJGL.currentLoop).getFrameID();
    }

    private void generateStructures(ProcMapChunk chunk, int chunkX, int chunkY) {
//        if(id != GameMap.ORE_LAYER) return;
//        if(chunkX == 0 && chunkY == 0) {
//            new CrashSpaceshipStructure(new Vector2i(5, 5), map.getTilesets()).set(this);
//        } else if(Math.random() < IceCometStructure.chance) {
//            final int HALF_CHUNK_SIZE = ProcMapChunk.CHUNK_SIZE / 2;
//            new IceCometStructure(new Vector2i((chunkX * 2 + 1) * HALF_CHUNK_SIZE, (chunkY * 2 + 1) * HALF_CHUNK_SIZE), map.getTilesets()).set(this);
//        }
    }

    public void saveChunksNotIn(Set<Point> usingChunkPositions) {
        synchronized(inMemory) {
            Set<Point> loadedChunkPosition = inMemory.keySet();
            for (Iterator<Point> it = loadedChunkPosition.iterator(); it.hasNext();) {
                Point pt = it.next();
                ProcMapChunk save = inMemory.get(pt);
                if(!usingChunkPositions.contains(pt)) {
                    it.remove();
                    ChunkThread.registerSaveChunk(new ChunkLocation.Save(map.getMapLayer(GameMap.TURF_LAYER), pt.x, pt.y) {
                        @Override
                        public void afterSaving(ProcMapChunk chunk) {
                        }

                        @Override
                        public void saveFailed() {
                        }
                    }, save);
                }
            }
        }
    }

    public void registerChunk(int x, int y) {
        ChunkThread.registerLoadChunk(new ChunkLocation.Load(map.getMapLayer(GameMap.TURF_LAYER), x, y) {
            @Override
            public void afterLoading(ProcMapChunk chunk) {
                synchronized(inMemory) {
                    inMemory.put(new Point(x, y), chunk);
                }
            }

            @Override
            public void loadFailed() {
            }
        });
    }
}
