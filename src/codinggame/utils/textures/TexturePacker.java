/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils.textures;

import com.lwjglwrapper.opengl.objects.TextureData;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Welcome
 */
public class TexturePacker {
    
    private PackedTexture texture;
    private int tileWidth, tileHeight;

    public TexturePacker(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }
    
    public void reset(int width, int height, boolean deleteExist) {
        if(deleteExist) texture.dispose();
        texture = new PackedTexture(width, height, tileWidth, tileHeight){
            @Override
            public void configTexture(int id) {
                GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(textureType, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            }
        
            
            
        };
    }
    
    public void create(int width, int height) {
        reset(width, height, false);
    }
    
    public void addTexture(Object id, TextureData data, int x, int y) {
        texture.add(id, data, x, y);
    }

    public PackedTexture getTexture() {
        return texture;
    }
    
}
