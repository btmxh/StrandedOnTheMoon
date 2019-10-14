/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import com.lwjglwrapper.opengl.objects.Texture2D;
import java.io.Serializable;

/**
 *
 * @author Welcome
 */
public class ItemType implements Serializable{
    private transient final Texture2D texture;
    private final String name;
    
    public ItemType(Texture2D texture, String name) {
        this.texture = texture;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Texture2D getTexture() {
        return texture;
    }
    
    public static class Mass extends ItemType{
        
        public Mass(Texture2D texture, String name) {
            super(texture, name);
        }
        
    }
    
    public static class Count extends ItemType{
        
        private final double massPerItem;

        public Count(Texture2D texture, String name, double massPerItem) {
            super(texture, name);
            this.massPerItem = massPerItem;
        }
        
        public double getMassPerItem() {
            return massPerItem;   
        }
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
