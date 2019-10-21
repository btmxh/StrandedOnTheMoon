/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.chunkloading;

import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLayer;
import java.awt.Point;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public abstract class ChunkLocation {
    
    private ProcMapLayer layer;
    private int chunkX, chunkY;
    private boolean done = false;

    public ChunkLocation(ProcMapLayer layer, int chunkX, int chunkY) {
        this.layer = layer;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }
    
    public abstract void afterLoading(ProcMapChunk chunk);
    public abstract void loadFailed();
    public abstract void afterSaving(ProcMapChunk chunk);
    public abstract void saveFailed();
    
    
    public void load() {
        if(done)    return;
        try {
            String file = layer.getMap().getChunkDirectory(layer.getID(), chunkX, chunkY);
            ProcMapChunk chunk = ChunkIO.loadChunk(file, layer.getMap().getTileset());
            afterLoading(chunk);
            done = true;
            ChunkThread.removeLoadChunk(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            loadFailed();
        }
    }
    
    public void save(ProcMapChunk chunk) {
        if(done)    return;
        String file = layer.getMap().getChunkDirectory(layer.getID(), chunkX, chunkY);
        try {
            ChunkIO.writeChunk(file, chunk);
            afterSaving(chunk);
            done = true;
            ChunkThread.removeSaveChunk(this);
        } catch (IOException ex) {
            saveFailed();
        }
    }

    @Override
    public int hashCode() {
        int lid = layer.getID();
        return Objects.hash(lid, chunkX, chunkY);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null? true:obj.getClass() != this.getClass()) return false;
        ChunkLocation that = (ChunkLocation) obj;
        return this.layer == that.layer && this.chunkX == that.chunkX && this.chunkY == that.chunkY;
    }

    @Override
    public String toString() {
        return done + " " + layer.getID() + " " + chunkX + " " + chunkY;
    }
    
    public static abstract class Load extends ChunkLocation {

        public Load(ProcMapLayer layer, int chunkX, int chunkY) {
            super(layer, chunkX, chunkY);
        }

        @Override
        public void saveFailed() {}

        @Override
        public void afterSaving(ProcMapChunk chunk) {}

    }
    
    public static abstract class Save extends ChunkLocation {

        public Save(ProcMapLayer layer, int chunkX, int chunkY) {
            super(layer, chunkX, chunkY);
        }

        @Override
        public void loadFailed() {}
        
        @Override
        public void afterLoading(ProcMapChunk chunk) {}
        
    }
    
}
