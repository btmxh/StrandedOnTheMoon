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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            lock.notifyAll();
        }
    }
    
    public static void registerSaveChunk(ChunkLocation location, ProcMapChunk chunk) {
        startThread();
        synchronized (lock) {
            chunksToSave.put(location, chunk);
            lock.notifyAll();
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
        synchronized (lock) {
            lock.notifyAll();
        }
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
        ChunkLocation load = null, save = null;
        ProcMapChunk saveChunk = null;
        while (running) {
            
            synchronized (lock) {
                Optional<ChunkLocation> loadOptional = chunksToLoad.stream().findAny();
                load = loadOptional.isPresent()? loadOptional.get():null;
                Optional<Map.Entry<ChunkLocation, ProcMapChunk>> saveOptional = chunksToSave.entrySet().stream().findAny();
                Map.Entry<ChunkLocation, ProcMapChunk> entry = saveOptional.isPresent()? saveOptional.get():null;
                if(entry != null) {
                    save = entry.getKey();
                    saveChunk = entry.getValue();
                }
                if(load == null && save == null)    try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ChunkThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(load != null) {
                load.load();
                chunksToLoad.remove(load);
            }
            if(save != null) {
                save.save(saveChunk);
                chunksToSave.remove(save);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChunkThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
