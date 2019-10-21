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
public class GeneratedStructure extends Structure{
    protected double generationRatio;   //0.5 -> generate about half the original structure

    public GeneratedStructure(Vector2i origin, Map<Point, MapCell> tiles, double generationRatio) {
        super(origin, tiles);
        this.generationRatio = generationRatio;
    }

    @Override
    public void set(MapLayer map) {
        for (Point point : tiles.keySet()) {
            if(Math.random() < generationRatio) {
                map.setTileAt(point.x + origin.x, point.y + origin.y, tiles.get(point));
            }
        }
    }
    
    
}
