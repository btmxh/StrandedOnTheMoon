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
public class XMLMissionPage extends XMLPage{
    private String title, content;

    public XMLMissionPage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public void render(NVGGraphics g, float pageWidth, float pageHeight) {
        GameUIHandler.textFont.use();
        g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        g.textPaint(StaticColor.BLACK);
        g.textSize(TITLE_TEXT_SIZE);
        
        g.text(title, OFFSET, OFFSET);
        
        g.textSize(CONTENT_TEXT_SIZE);
        g.textBox(content, OFFSET, OFFSET + TITLE_TEXT_SIZE, pageWidth - OFFSET * 2);
    }
    
    
    
    
}
