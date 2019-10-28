/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

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
    public static final int MOON_TURF = 2, COPPER_ORE = 1, CHARGER = 3, BARRIER = 4, STORAGE_CELL = 5, MOON_ROCK = 6, CENTRAL_STORAGE_UNIT = 7, CHEMICAL_PROCESSOR = 8, CONVEYOR = 9, GLASS = 10, SOIL = 11, WHEAT_CROPS = 12;
    
    private int id;
    private transient Texture2D texture;

    public MapTile(int id, Texture2D texture) {
        this.id = id;
        this.texture = texture;
    }
    
    public int getID() {
        return id;
    }
    
    public Texture2D getTexture() {
        return texture;
    }

    public String getName() {
        switch (id) {
            case BARRIER: return "Barrier";
            case CENTRAL_STORAGE_UNIT: return "Central Storage Unit";
            case CHARGER: return "Charger";
            case CHEMICAL_PROCESSOR: return "Chemical Processor";
            case CONVEYOR: return "Conveyor";
            case COPPER_ORE: return "Copper Ore";
            case MOON_ROCK: return "Moon Rock";
            case MOON_TURF: return "Moon Turf";
            case STORAGE_CELL: return "Storage Cell";
            case GLASS: return "Glass";
            default: return "";
        }
    }
}
