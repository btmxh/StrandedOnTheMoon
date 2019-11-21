/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Welcome
 */
public class EntityLoader {
    
    public static List<EntityData> loadEntities(File saveFile) throws IOException {
        FileInputStream stream = new FileInputStream(saveFile);
        FileChannel channel = stream.getChannel();
        ByteBuffer buffer;
        buffer = ByteBuffer.allocate(Integer.BYTES);
        channel.read(buffer);
        buffer.flip();
        int stride = buffer.getInt();
        List<EntityData> entities = new ArrayList<>();
        while(true) {
            ByteBuffer b = ByteBuffer.allocate(stride);
            int read = channel.read(b);
            if(read == -1)  break;
            b.position(0);
            entities.add(new EntityData(b));
        }
        return entities;
    }
    
    public static void writeEntities(File saveFile, Iterable<EntityData> entities, final int stride) throws IOException {
        FileOutputStream stream = new FileOutputStream(saveFile);
        FileChannel channel = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(stride);
        buffer.position(0);
        channel.write(buffer);
        for (EntityData entity : entities) {
            entity.getBuffer().position(0);
            channel.write(entity.getBuffer());
        }
        stream.flush();
        stream.close();
    }

    public static void main(String[] args) throws IOException {
        final int STRIDE = 10;
        Random random = new Random();
        byte[] arr1 = new byte[5];
        random.nextBytes(arr1);
        ByteBuffer buf1 = ByteBuffer.allocate(STRIDE);
        buf1.put(arr1);
        byte[] arr2 = new byte[7];
        random.nextBytes(arr2);
        ByteBuffer buf2 = ByteBuffer.allocate(STRIDE);
        buf2.put(arr2);
        
        List<EntityData> entities = Arrays.asList(new EntityData(buf1), new EntityData(buf2));
        writeEntities(new File("saves/procmap/entities.bin"), entities, STRIDE);
        
        List<EntityData> loadEntities = loadEntities(new File("saves/procmap/entities.bin"));
        
        byte[] load;
        load = loadEntities.get(0).getBuffer().array();
        System.out.println(Arrays.toString(load));
        System.out.println(Arrays.toString(arr1));
        load = loadEntities.get(1).getBuffer().array();
        System.out.println(Arrays.toString(load));
        System.out.println(Arrays.toString(arr2));
        
    }
    
}
