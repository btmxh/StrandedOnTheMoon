/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content.pages;

import codinggame.handlers.GameUIHandler;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.colors.StaticColor;
import org.lwjgl.nanovg.NanoVG;
import static codinggame.ui.books.content.pages.Constants.*;

/**
 *
 * @author Welcome
 */
public class XMLCenteredPage extends XMLPage{
    private String text;
    private StaticColor color;

    public XMLCenteredPage(String text, StaticColor color) {
        this.text = text;
        this.color = color;
    }    

    @Override
    public void render(NVGGraphics g, float pageWidth, float pageHeight) {
        g.textAlign(NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        GameUIHandler.textFont.use();
        g.textPaint(color);
        g.textSize(CENTERED_TEXT_SIZE);
        
        g.text(text, pageWidth / 2, pageHeight / 2);
    }
}
