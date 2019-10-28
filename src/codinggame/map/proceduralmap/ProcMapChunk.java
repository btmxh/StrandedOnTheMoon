/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.CodingGame;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.cells.DataCell;
import com.lwjglwrapper.LWJGL;
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
    private Map<Point, DataCell> dataCells;
    long lastFrameUsed;

    public ProcMapChunk() {
        map = new ProcMapCell[CHUNK_SIZE][CHUNK_SIZE];
        lastFrameUsed = CodingGame.getInstance().getFrameID();
        dataCells = new HashMap<>();
    }

    public void setTileAt(int x, int y, MapTile tile) {
        setTileAt(x, y, new ProcMapCell(tile));
    }
    
    public void setTileAt(int x, int y, ProcMapCell cell) {
        if(x < 0 | x >= CHUNK_SIZE | y < 0 | y >= CHUNK_SIZE)    return;
        map[x][y] = cell;
        if(cell instanceof DataCell) {
            dataCells.put(new Point(x, y), (DataCell) cell);
        }
    }
    
    public ProcMapCell getTileAt(int x, int y) {
        if(x < 0 | x >= CHUNK_SIZE | y < 0 | y >= CHUNK_SIZE)    return null;
        return map[x][y];
    }

    @Override
    public String toString() {
        return Arrays.deepToString(map);
    }

    void println() {
        for (int y = 0; y < CHUNK_SIZE; y++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                System.out.print(getTileAt(x, y).getTileType() + " ");
            }
            System.out.println();
        }
    }

    void renderAsImage(String path) {
        BufferedImage image = new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                image.setRGB(x, y, 0);
                ProcMapCell cell = getTileAt(x, y);
                if(cell != null? cell.getTileType() != null:false) {
                    image.setRGB(x, y, cell.getTileType().getID() * 4554);
                }
            }
        }
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException ex) {
            Logger.getLogger(ProcMapChunk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ProcMapCell[][] getMap() {
        return map;
    }

    public Map<Point, DataCell> getDataCells() {
        return dataCells;
    }
    
    public void setDataCells(Map<Point, DataCell> dataCells, MapTilesets tilesets) {
        for (Point pt : dataCells.keySet()) {
            ProcMapCell cell = getTileAt(pt.x, pt.y);
            if(cell != null? cell.getTileType() != null:false) {    
                dataCells.get(pt).setTileType(cell.getTileType());
            }
            dataCells.get(pt).updateSavedData(tilesets);
            setTileAt(pt.x, pt.y, dataCells.get(pt));
        }
    }

    public void putDataCell(int x, int y, DataCell dataCell) {
        dataCells.put(new Point(x, y), dataCell);
    }
    
    
    
}
