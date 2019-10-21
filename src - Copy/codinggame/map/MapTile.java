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
    public static final int MOON_TURF = 2, COPPER_ORE = 1, CHARGER = 3, BARRIER = 4, STORAGE_CELL = 5, MOON_ROCK = 6;
    
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
    
    public static void main(String[] args) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("saves/test.txt"));
        oos.writeObject(new MapTile(69, null));
        oos.close();
    }
}
