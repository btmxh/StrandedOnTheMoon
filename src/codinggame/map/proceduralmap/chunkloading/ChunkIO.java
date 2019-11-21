/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.chunkloading;

import codinggame.map.MapCell;
import codinggame.map.MapTileset;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.proceduralmap.entities.EntityLoader;
import codinggame.utils.ByteArray;
import codinggame.utils.ChannelUtils;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class ChunkIO {
    
    public static ProcMapChunk loadChunk(String path, MapTileset tileset) throws IOException {
        File chunkFile = new File(path);
        File chunkEntityFile = new File(path + "e");
        if(chunkFile.exists()) {
            FileInputStream inputStream = new FileInputStream(chunkFile);
            FileChannel channel = inputStream.getChannel();
            int length = ProcMapChunk.CHUNK_SIZE * ProcMapChunk.CHUNK_SIZE;
            ProcMapChunk chunk = new ProcMapChunk();
            for (int i = 0; i < length; i++) {
                int x = i / ProcMapChunk.CHUNK_SIZE;
                int y = i % ProcMapChunk.CHUNK_SIZE;
                int stride = ChannelUtils.readInt(channel);
                if(stride == 0) chunk.setTileAt(x, y, null);
                else {
                    byte[] arr = ChannelUtils.read_arr(channel, stride);
                    chunk.setTileAt(x, y, new ProcMapCell(arr, stride));
                }
            }
            inputStream.close();
            
            if(chunkEntityFile.exists()) {
                List<EntityData> entities = EntityLoader.loadEntities(chunkEntityFile);
                Function<EntityData, Point> keyMapper = (data) -> {
                    Vector2i pt = data.getPosition();
                    return new Point(pt.x, pt.y);
                };
                Map<Point, EntityData> entityMap = entities.stream().collect(Collectors.toMap(keyMapper, e -> e));
                chunk.setEntities(entityMap);
            }
            return chunk;
        } else {
            return null;
        }
    }
    
    public static void writeChunk(String path, ProcMapChunk chunk) throws IOException {
        if(chunk == null)   return;
        File chunkFile = new File(path);
        File chunkEntityFile = new File(path + "e");
        FileChannel channel = FileChannel.open(chunkFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        for (int x = 0; x < ProcMapChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < ProcMapChunk.CHUNK_SIZE; y++) {
                ProcMapCell cell = chunk.getTileAt(x, y);
                if(MapCell.nullCheck(cell)) {
                    cell.write(channel);
                } else {
                    ChannelUtils.writeInt(channel, 0);
                }
            }
        }
        channel.close();
        
        if(chunk.getEntities().isEmpty())  return;
        EntityLoader.writeEntities(chunkEntityFile, chunk.getEntities().values(), EntityData.STRIDE);
    }
    
}
