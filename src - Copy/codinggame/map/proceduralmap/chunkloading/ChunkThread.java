/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.chunkloading;

import codinggame.map.proceduralmap.ProcMapChunk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class ChunkThread implements Runnable{
    
    private static final HashSet<ChunkLocation> chunksToLoad = new HashSet<>();
    private static final List<ChunkLocation> remove = new ArrayList<>();
    
    private static final Map<ChunkLocation, ProcMapChunk> chunksToSave = new HashMap<>();
    private static final List<ChunkLocation> savedChunks = new ArrayList<>();
    
    private static final Object lock = new Object();
    
    private static Thread thread;
    private static volatile boolean running = true;
    
    public static void registerLoadChunk(ChunkLocation location) {
        startThread();
        synchronized (lock) {
            chunksToLoad.add(location);
        }
    }
    
    public static void registerSaveChunk(ChunkLocation location, ProcMapChunk chunk) {
        startThread();
        synchronized (lock) {
            chunksToSave.put(location, chunk);
        }
    }
    
    public static void startThread() {
        if(thread == null) {
            thread = new Thread(new ChunkThread(), "Chunk Thread");
            running = true;
            thread.start();
        }
    }
    
    public static void stopThread() {
        running = false;
    }
    
    static void removeLoadChunk(ChunkLocation location) {
        synchronized (lock) {
            remove.add(location);
        }
    }
    
    static void removeSaveChunk(ChunkLocation location) {
        synchronized (lock) {
            savedChunks.add(location);
        }
    }

    @Override
    public void run() {
        while (running) {
            System.out.print("");
            synchronized (lock) {
                if(!chunksToLoad.isEmpty()) {
                    chunksToLoad.forEach(ChunkLocation::load);
                }
                chunksToLoad.removeAll(remove);
                if(!chunksToSave.isEmpty()) {
                    for (ChunkLocation location : chunksToSave.keySet()) {
                        location.save(chunksToSave.get(location));
                    }
                }
                chunksToSave.keySet().removeAll(savedChunks);
            }
        }
    }
    
}
