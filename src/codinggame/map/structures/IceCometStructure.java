/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ProcMapCell;
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
    
    public IceCometStructure(Vector2i origin, MapTilesets tileset) {
        super(origin, generate(tileset));
    }

    private static Map<Point, MapCell> generate(MapTilesets tileset) {
        Map<Point, MapCell> map = new HashMap<>();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                if(x*x + y*y > RADIUS * RADIUS) continue;
                double rand = Math.random();
                if(rand > 0.4)  continue;
                if(rand > 0.7)  map.put(new Point(x, y), new ProcMapCell(tileset.getTileByID(MapTile.MOON_ROCK)));
                else map.put(new Point(x, y), new ProcMapCell(tileset.getTileByID(MapTile.ICE)));
            }
        }
        return map;
    }
    
    
}
