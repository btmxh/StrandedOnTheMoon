/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.CodingGame;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapLayers;
import codinggame.map.MapTileset;
import codinggame.map.MapTilesets;
import codinggame.map.cells.DataCell;
import codinggame.objs.Clock;
import codinggame.utils.Point3i;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class ProcMap extends GameMap{
    
    private String path;
    private long seed;
    private ChunkGenerator generator;

    public ProcMap(String path, long seed, MapTilesets tilesets, MapLayers mapLayers) {
        super(tilesets, mapLayers);
        this.path = path;
        this.seed = seed;
        this.generator = new ChunkGenerator(seed);
    }
    
    public String getChunkDirectory(int layer, int chunkX, int chunkY) {
        return path + "/chunk" + layer + "_" + chunkX + "_" + chunkY + ".cnk";
    }
    
    public MapTileset getTileset() {
        return getTilesets().tilesets[0];
    }

    @Override
    public ProcMapLayer getMapLayer(int id) {
        return (ProcMapLayer) super.getMapLayer(id); //To change body of generated methods, choose Tools | Templates.
    }

    public ChunkGenerator getChunkGenerator() {
        return generator;
    }
    
    public void update(int cameraChunkX, int cameraChunkY) {
        getMapLayers().stream().map((l) -> (ProcMapLayer) l).forEach((layer) -> layer.update(cameraChunkX, cameraChunkY));
    }
    
    public void save(Clock clock) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/map.dat"));
        writer.write(String.valueOf(seed));
        writer.newLine();
        writer.write(String.valueOf(clock.getGameTime()));
        writer.close();
    }
    
    
}
