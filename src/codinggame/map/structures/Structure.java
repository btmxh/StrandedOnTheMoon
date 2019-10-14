/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import java.awt.Point;
import java.util.Map;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class Structure {
    protected Vector2i origin;
    protected Map<Point, MapCell> tiles;

    public Structure(Vector2i origin, Map<Point, MapCell> tiles) {
        this.origin = origin;
        this.tiles = tiles;
    }
    
    public void set(MapLayer map) {
        for (Point point : tiles.keySet()) {
            map.setTileAt(point.x + origin.x, point.y + origin.y, tiles.get(point).clone());
        }
    }
}
