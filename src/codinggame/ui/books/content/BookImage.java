/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.NVGImage;

/**
 *
 * @author Welcome
 */
public class BookImage {
    private NVGImage image;

    public BookImage(String imagePath) {
        image = LWJGL.graphics.createNanoVGImage(imagePath, 0);
    }

    public void render(NVGGraphics g, float x, float y, float width) {
        float height = width * image.getHeight() / image.getWidth();
        g.image(image, x, y, width, height);
    }
    
}
