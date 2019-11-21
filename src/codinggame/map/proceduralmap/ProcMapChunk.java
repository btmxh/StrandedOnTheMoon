/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.CodingGame;
import static codinggame.handlers.ObjectChooseHandler.ChoosingObject.tile;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.entities.EntityData;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Welcome
 */
public class ProcMapChunk {
    public static final int CHUNK_SIZE = 16;
    
    private ProcMapCell[][] map;
    private Map<Point, EntityData> entities;
    long lastFrameUsed;
    private Listener listener = (x, y, o, n) -> {};
    

    public ProcMapChunk() {
        map = new ProcMapCell[CHUNK_SIZE][CHUNK_SIZE];
        lastFrameUsed = CodingGame.getInstance().getFrameID();
        entities = new HashMap<>();
    }

    public void setTileAt(int x, int y, int tileID) {
        setTileAt(x, y, ProcMapCell.createCell(tileID));
    }
    
    public void setTileAt(int x, int y, ProcMapCell cell) {
        if(x < 0 | x >= CHUNK_SIZE | y < 0 | y >= CHUNK_SIZE)    return;
        listener.changed(x, y, map[x][y], cell);
        map[x][y] = cell;
    }
    
    public ProcMapCell getTileAt(int x, int y) {
        if(x < 0 | x >= CHUNK_SIZE | y < 0 | y >= CHUNK_SIZE)    return null;
        return map[x][y];
    }

    @Override
    public String toString() {
        return Arrays.deepToString(map);
    }
    
    public ProcMapCell[][] getMap() {
        return map;
    }
    
    public void setListener(Listener listener) {
        if(listener == null)    listener = (x, y, o, n) -> {};
        this.listener = listener;
    }

    public EntityData getEntityAt(int tileX, int tileY) {
        return entities.get(new Point(tileX, tileY));
    }

    public void setEntityAt(int tileX, int tileY, EntityData data) {
        if(data == null)    entities.remove(new Point(tileX, tileY));
        else entities.put(new Point(tileX, tileY), data);
    }

    public Listener getListener() {
        return listener;
    }
    
    @FunctionalInterface
    public interface Listener {
        public void changed(int x, int y, ProcMapCell oldValue, ProcMapCell newValue);
    }

    /**
     * @return the entities
     */
    public Map<Point, EntityData> getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(
            Map<Point, EntityData> entities) {
        this.entities = entities;
    }
    
}
