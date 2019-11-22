/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.GameMap;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.structures.IceCometStructure;
import com.lwjglwrapper.utils.OpenSimplexNoise;
import java.nio.ByteBuffer;
import org.joml.Vector2i;

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
        for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                chunk.setTileAt(x, y, MapTile.MOON_TURF);
            }
        }
        for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                int tileX = chunkX * ProcMapChunk.CHUNK_SIZE + x;
                int tileY = chunkY * ProcMapChunk.CHUNK_SIZE + y;
                double noiseValue = noise.eval(tileX * VEIN_SIZE, tileY * VEIN_SIZE);
                if(noiseValue < IRON) {
                    chunk.setEntityAt(x, y, createEntityData(tileX, tileY, MapTile.IRON_ORE));
                } else if(noiseValue < COPPER) {
                    chunk.setEntityAt(x, y, createEntityData(tileX, tileY, MapTile.COPPER_ORE));
                } else if(noiseValue < ROCK) {
                    chunk.setEntityAt(x, y, createEntityData(tileX, tileY, MapTile.MOON_ROCK));
                }
            }
        }
        IceCometStructure iceCometStructure = new IceCometStructure(new Vector2i(7, 7));
        iceCometStructure.set(chunk, chunkX, chunkY);
        return chunk;
    }
    
    public static EntityData createEntityData(int x, int y, int tileID) {
        EntityData data = EntityData.empty();
        ByteBuffer buf = data.getBuffer();
        buf.position(EntityData.POSITION_2D);
        buf.putInt(x);
        buf.putInt(y);
        buf.put(EntityData.TILE_ENTITY);
        buf.putInt(tileID);
        buf.putInt(0);
        buf.position(EntityData.HEIGHT);
        buf.putFloat(Float.NaN);
        buf.position(0);
        
        return data;
    }
    
}
