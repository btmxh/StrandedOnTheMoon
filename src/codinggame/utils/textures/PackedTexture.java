/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils.textures;

import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.joml.Rectanglef;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class PackedTexture extends Texture2D{

    private int width, height;
    private int tileWidth, tileHeight;
    private Map<Object, Point> textureCoordinates;

    public PackedTexture(int width, int height, int tileWidth, int tileHeight) {
        super(new TextureData(width * tileWidth, height * tileHeight, null));
        this.textureCoordinates = new HashMap<>();
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    void add(Object id, TextureData data, int x, int y) {
        super.modifyTexture(data, x * tileWidth, y * tileHeight);
        textureCoordinates.put(id, new Point(x, y));
    }
    
    public Vector2f getTextureCoordinates(Object tex) {
        Point position = textureCoordinates.get(tex);
        return new Vector2f((float) position.x / width, (float) position.y / height);
    }
    
    public Rectanglef getTextureBounds(Object tex) {
        Point position = textureCoordinates.get(tex);
        Vector2f min = new Vector2f((float) position.x / width, (float) position.y / height);
        Vector2f max = new Vector2f((float) (position.x + 1) / width, (float) (position.y + 1) / height);
        return new Rectanglef(min, max);
    }
    
}
