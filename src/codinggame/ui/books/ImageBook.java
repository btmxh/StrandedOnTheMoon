/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books;

import codinggame.globjs.RenderableTexture;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public class ImageBook implements Book{
    private final int COVER = -2, LAST_COVER;
    
    private final int pageCount;
    private int pageIndex = COVER;
    private String path;
    
    public ImageBook(String path) {
        this.path = path;
        String count = null;
        try {
            String meta = Utils.loadResourceAsString(ImageBook.class, path + "/book_info.inf");
            String[] lines = meta.split("\n");
            count = lines[0];
            
        } catch (IOException ex) {
            Logger.getLogger(ImageBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        pageCount = Integer.parseInt(count);
        LAST_COVER = pageCount;
    }
    
    public Texture2D[] getCurrentPages() {
        return new Texture2D[]{
            loadPage(pageIndex),
            loadPage(pageIndex + 1)
        };
    }
    
    public void turnRight() {
        pageIndex+=2;
        renders = null;
    }
    
    public void turnLeft() {
        pageIndex-=2;
        renders = null;
    }
    
    private Texture2D loadTexture(String path) {
        try {
            return new Texture2D(TextureData.fromResource(ImageBook.class, this.path + "/" + path + ".png"));
        } catch (NullPointerException npe) {
            return null;
        }
    }
    
    private Texture2D loadPage(int page) {
        if(page == COVER + 1)   return loadTexture("cover");
        else if(page == LAST_COVER) return loadTexture("last_cover");
        else return loadTexture("page ("  + page + ")");
    }
    
    private Texture2D[] renders;
    public void render() {
        if(renders == null)     renders = getCurrentPages();
        Rectanglef bounds = new Rectanglef(0, 0, 105 * 3, 128 * 3);

        for (int i = 0; i < renders.length; i++) {
            Texture2D texture = renders[i];
            renderTexture(texture, bounds);
            float width = bounds.maxX - bounds.minX;
            bounds.minX = bounds.maxX;
            bounds.maxX += width;
        }
        
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_RIGHT)) {
            turnRight();
        } else if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_LEFT)) {
            turnLeft();
        }
        
        System.out.println(Arrays.toString(renders));
    }
    
    private RenderableTexture rt;
    private void renderTexture(Texture2D texture, Rectanglef bounds) {
        if(texture == null) return;
        if(rt == null) {
            rt = new RenderableTexture(bounds, texture);
        } else {
            rt.setTexture(texture);
            rt.setBounds(bounds);
        }
        rt.render(false);
    }
    
    public static void main(String[] args) {
        ImageBook b = new ImageBook("/books/guidebook");
        System.out.println(b.pageCount);
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public void goToPage(int page) {
        this.pageIndex = page;
    }

    @Override
    public void close() {
        this.pageIndex = COVER;
    }
}
