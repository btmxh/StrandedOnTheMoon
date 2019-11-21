/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.map.tiles.MapTiles;
import com.lwjglwrapper.opengl.objects.Texture2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Welcome
 */
public class MapTile implements Serializable{
    public static final int NULL = -1, MOON_TURF = 2, COPPER_ORE = 1, CHARGER = 3, BARRIER = 4, STORAGE_CELL = 5, MOON_ROCK = 6, CENTRAL_STORAGE_UNIT = 7, CHEMICAL_PROCESSOR = 8, CONVEYOR = 9, GLASS = 10, SOIL = 11, POTATO_CROPS = 12, ICE = 13, IRON_ORE = 14;
    public static final int LAST = IRON_ORE;
    
    private int id;
    private transient Texture2D texture;

    public MapTile(int id, Texture2D texture) {
        this.id = id;
        this.texture = texture;
        if(texture == null) throw new IllegalArgumentException("texture == null");
    }
    
    public int getID() {
        return id;
    }
    
    public Texture2D getTexture() {
        return texture;
    }

    public String getName() {
        return MapTiles.get(id).getName();
    }

    public boolean isSolid() {
        return MapTiles.get(id).isSolid();
    }
}
