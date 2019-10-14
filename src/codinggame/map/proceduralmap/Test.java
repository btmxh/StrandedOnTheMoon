/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.proceduralmap.chunkloading.ChunkIO;
import codinggame.map.GameMap;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Welcome
 */
public class Test {
    
    public static void main(String[] args) throws IOException {
        ProcMap map = ProcMapLoader.loadMap("saves/procmap", "", null);
        ProcMapLayer layer = map.getMapLayer(GameMap.ORE_LAYER);
        String dir = map.getChunkDirectory(GameMap.ORE_LAYER, 0, 0);
        ChunkIO.writeChunk(dir, layer.getChunk(0, 0));
        ChunkIO.loadChunk(dir, map.getTileset()).renderAsImage("D:\\image.jpg");
        new File(dir).delete();
        //while(true) {}
    }
    
}
