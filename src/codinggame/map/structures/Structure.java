/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.proceduralmap.CellUtils;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import java.awt.Point;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class Structure {
    protected Vector2i origin;
    protected Map<Point, MapCell> tiles;
    protected Map<Point, EntityData> entities;

    public Structure(Vector2i origin, Map<Point, MapCell> tiles, Map<Point, EntityData> entities) {
        this.origin = origin;
        this.tiles = tiles;
        this.entities = entities;
    }
    
    public void set(ProcMapChunk chunk, int chunkX, int chunkY) {
        for (Point point : tiles.keySet()) {
            chunk.setTileAt(point.x + origin.x, point.y + origin.y, CellUtils.clone((ProcMapCell) tiles.get(point)));
        }
        for (Point point : entities.keySet()) {
            EntityData data = entities.get(point);
            data.setHeight(Float.NaN);
            data.setPosition(new Vector2f(point.x + origin.x + chunkX * ProcMapChunk.CHUNK_SIZE, point.y + origin.y + chunkY * ProcMapChunk.CHUNK_SIZE));
            chunk.setEntityAt(point.x + origin.x + chunkX * ProcMapChunk.CHUNK_SIZE, point.y + origin.y + chunkY * ProcMapChunk.CHUNK_SIZE, entities.get(point));
        }
    }
}
