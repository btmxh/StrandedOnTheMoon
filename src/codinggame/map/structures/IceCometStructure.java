/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ChunkGenerator;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.entities.EntityData;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class IceCometStructure extends Structure{
    public static final int RADIUS = 2;
    public static final double chance = 0.04f;   //avg. 4 chunks with this structure per 100 chunks
    
    public IceCometStructure(Vector2i origin) {
        super(origin, cells(), entities());
    }

    private static Map<Point, MapCell> cells() {
        Map<Point, MapCell> map = new HashMap<>();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                if(x*x + y*y > RADIUS * RADIUS) continue;
                double rand = Math.random();
                if(rand > 0.4)  continue;
                if(rand > 0.7)  map.put(new Point(x, y), ProcMapCell.createCell(MapTile.MOON_ROCK));
                else map.put(new Point(x, y), ProcMapCell.createCell(MapTile.ICE));
            }
        }
        return new HashMap<>();
    }
    
    private static Map<Point, EntityData> entities() {
        Map<Point, EntityData> map = new HashMap<>();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                if(x*x + y*y > RADIUS * RADIUS) continue;
                double rand = Math.random();
                if(rand > 0.4)  continue;
                if(rand > 0.7)  map.put(new Point(x, y), ChunkGenerator.createEntityData(0, 0, MapTile.MOON_ROCK));
                else map.put(new Point(x, y), ChunkGenerator.createEntityData(0, 0, MapTile.ICE));
            }
        }
        return map;
    }
    
    
}
