/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.utils.Utils;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 *
 * @author Welcome
 */
public class ItemType implements Serializable{
    private transient NVGImage nvgTexture;
    private transient final Texture2D texture;
    private final String name;
    
    public ItemType(String path, String name) {
        this.texture = new Texture2D(TextureData.fromResource(ItemType.class, path)){
            @Override
            public void configTexture(int id) {
                GL13.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL13.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }
        };
        try {
            this.nvgTexture = LWJGL.graphics.createNanoVGImageFromResource(
                    Utils.ioResourceToByteBuffer(ItemType.class.getResourceAsStream(path), 8 * 1024),
                    NanoVG.NVG_IMAGE_GENERATE_MIPMAPS);
        } catch (Exception ex) {}
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Texture2D getTexture() {
        return texture;
    }

    public NVGImage getNanoVGTexture() {
        return nvgTexture;
    }
    
    public void dispose() {
        nvgTexture.dispose();
    }
    
    public static class Mass extends ItemType{
        
        public Mass(String path, String name) {
            super(path, name);
        }
        
    }
    
    public static class Count extends ItemType{
        
        private final double massPerItem;

        public Count(String path, String name, double massPerItem) {
            super(path, name);
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
