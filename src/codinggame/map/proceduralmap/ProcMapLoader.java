/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.GameMap;
import codinggame.map.MapLayers;
import codinggame.map.MapTile;
import codinggame.map.MapTileSheet;
import codinggame.map.MapTileset;
import codinggame.map.MapTilesets;
import codinggame.objs.Clock;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Welcome
 */
public class ProcMapLoader {
    
    public static ProcMap loadMap(String mapDirectory, String textureDirectory, Clock clock) throws IOException {
        //Example: saves/procmap
        
        File mapDataFile = new File(mapDirectory + "/map.dat");
        List<String> lines = loadResourceAsLines(new FileInputStream(mapDataFile));
        long seed = Long.parseLong(lines.get(0));
        long gameTime = Long.parseLong(lines.get(1));
        if(clock != null)   clock.setSavedTime(gameTime);
        System.out.println(gameTime);
        
        File tilesetFile = new File(mapDirectory + "/tileset.tst");
        MapTileset tileset = new MapTileset();
        lines = loadResourceAsLines(new FileInputStream(tilesetFile));
        
        int firstgid = Integer.parseInt(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            String[] properties = lines.get(i).split("\\s+", -1);
            if(properties[1].equalsIgnoreCase("sheet")) {
                properties = lines.get(i).split("\\s+", 5);
            } else {
                properties = lines.get(i).split("\\s+", 2);
            }
            String path = textureDirectory + "/" + properties[1];
            int id = Integer.parseInt(properties[0]) + firstgid;
            
            if(properties[1].equalsIgnoreCase("sheet")) {
                path = textureDirectory + "/" + properties[4];
                int noOfCols = Integer.parseInt(properties[2]);
                int noOfRows = Integer.parseInt(properties[3]);
                
                tileset.addTile(new MapTileSheet(noOfRows, noOfCols, id, new Texture2D(TextureData.fromResource(ProcMapLoader.class, path)){
                    @Override
                    public void configTexture(int id) {
                        GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                    }
                }));
            } else {
                tileset.addTile(new MapTile(id, new Texture2D(TextureData.fromResource(ProcMapLoader.class, path)){
                    @Override
                    public void configTexture(int id) {
                        GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                    }
                }));
            }
//            tileset.addTile(new MapTile(id, null));
        }
        
        MapLayers layers = new MapLayers();
        ProcMap map = new ProcMap(mapDirectory, seed, new MapTilesets(new MapTileset[]{tileset}), layers);
        layers.add(new ProcMapLayer(map, GameMap.TURF_LAYER));
        
        return map;
    }
    
    private static final List<String> loadResourceAsLines(InputStream stream) throws IOException {
        BufferedReader bis = new BufferedReader(new InputStreamReader(stream));
        List<String> lines = new ArrayList<>();
        String line;
        while((line = bis.readLine()) != null)  lines.add(line);
        return lines;
    } 
    
    private static final String loadResourceAsString(InputStream stream) throws IOException {
        return String.join("\n", loadResourceAsLines(stream));
    }    
}
