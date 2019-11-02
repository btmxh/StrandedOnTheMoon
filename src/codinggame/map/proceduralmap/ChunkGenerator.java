/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.GameMap;
import codinggame.map.MapTile;
import com.lwjglwrapper.utils.OpenSimplexNoise;

/**
 *
 * @author Welcome
 */
public class ChunkGenerator {

    private final long seed;
    private final OpenSimplexNoise noise;
    
    public static final double VEIN_SIZE = 0.3, ROCK = -0.6, COPPER = -0.7, IRON = -0.75;

    public ChunkGenerator(long seed) {
        this.seed = seed;
        this.noise = new OpenSimplexNoise(seed);
    }
    
    public ProcMapChunk generate(ProcMap map, int layer, int chunkX, int chunkY) {
        ProcMapChunk chunk = new ProcMapChunk();
        if(layer == GameMap.TURF_LAYER) {
            for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
                for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                    chunk.setTileAt(x, y, map.getTileset().getTile(MapTile.MOON_TURF));
                }
            }
        } else if(layer == GameMap.ORE_LAYER) {
            for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
                for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                    int tileX = chunkX * ProcMapChunk.CHUNK_SIZE + x;
                    int tileY = chunkY * ProcMapChunk.CHUNK_SIZE + y;
                    double noiseValue = noise.eval(tileX * VEIN_SIZE, tileY * VEIN_SIZE);
                    if(noiseValue < IRON) {
                        chunk.setTileAt(x, y, map.getTileset().getTile(MapTile.IRON_ORE));
                    } else if(noiseValue < COPPER) {
                        chunk.setTileAt(x, y, map.getTileset().getTile(MapTile.COPPER_ORE));
                    } else if(noiseValue < ROCK) {
                        chunk.setTileAt(x, y, map.getTileset().getTile(MapTile.MOON_ROCK));
                    }
                }
            }
        }
        return chunk;
    }
    
}
