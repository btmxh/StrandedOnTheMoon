/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.proceduralmap.CellUtils;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.entities.EntityData;
import java.awt.Point;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class GeneratedStructure extends Structure{
    protected double generationRatio;   //0.5 -> generate about half the original structure

    public GeneratedStructure(Vector2i origin, Map<Point, MapCell> tiles, Map<Point, EntityData> entities, double generationRatio) {
        super(origin, tiles, entities);
        this.generationRatio = generationRatio;
    }
    
    @Override
    public void set(ProcMapChunk chunk, int chunkX, int chunkY) {
        for (Point point : tiles.keySet()) {
            if(Math.random() >= generationRatio)    continue;
            chunk.setTileAt(point.x + origin.x, point.y + origin.y, CellUtils.clone((ProcMapCell) tiles.get(point)));
        }
        for (Point point : entities.keySet()) {
            if(Math.random() >= generationRatio)    continue;
            EntityData data = entities.get(point);
            data.setHeight(Float.NaN);
            data.setPosition(new Vector2f(point.x + origin.x + chunkX * ProcMapChunk.CHUNK_SIZE, point.y + origin.y + chunkY * ProcMapChunk.CHUNK_SIZE));
            chunk.setEntityAt(point.x + origin.x + chunkX * ProcMapChunk.CHUNK_SIZE, point.y + origin.y + chunkY * ProcMapChunk.CHUNK_SIZE, entities.get(point));
        }
    }
}
