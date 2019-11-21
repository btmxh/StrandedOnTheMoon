/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content.pages;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.utils.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Rectanglef;

/**
 *
 * @author Welcome
 */
public abstract class XMLPage {
    private static NVGImage left, right;
    
    public static void init() {
        try {
            left = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(XMLPage.class.getResourceAsStream("/books/images/left.png"), 8 * 1024), 0);
            right = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(XMLPage.class.getResourceAsStream("/books/images/right.png"), 8 * 1024), 0);
            System.out.println(left.toString());
        } catch (IOException ex) {
            Logger.getLogger(XMLPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public XMLPage() {
    }
    
    public XMLPage(boolean page, boolean cover) {
        
    } 
    
    protected abstract void render(NVGGraphics g, float pageWidth, float pageHeight);
    
    public static final int LEFT = 0, RIGHT = 1;
    public void render(NVGGraphics g, Rectanglef bounds, int side) {
        float pageWidth = bounds.maxX - bounds.minX;
        float pageHeight = bounds.maxY - bounds.minY;
        g.push();
        g.translate(bounds.minX, bounds.minY);
        switch (side) {
            case LEFT:  g.image(left, 0, 0, pageWidth, pageHeight);     break;
            case RIGHT: g.image(right, 0, 0, pageWidth, pageHeight);    break;
        }
        render(g, pageWidth, pageHeight);
        g.pop();
    }
}
