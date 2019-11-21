/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui.books.content.pages;

import codinggame.handlers.GameUIHandler;
import codinggame.ui.books.content.BookImage;
import codinggame.ui.books.content.XMLRequirement;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.colors.StaticColor;
import java.util.List;
import org.lwjgl.nanovg.NanoVG;
import static codinggame.ui.books.content.pages.Constants.*;

/**
 *
 * @author Welcome
 */
public class XMLMissionAdditionalPage extends XMLPage{
    
    private String missionDescription;
    private List<BookImage> images;
    private XMLRequirement requirement;

    public XMLMissionAdditionalPage(String missionDescription,
            List<BookImage> images, XMLRequirement requirement) {
        this.missionDescription = missionDescription;
        this.images = images;
        this.requirement = requirement;
    }

    @Override
    public void render(NVGGraphics g, float pageWidth, float pageHeight) {
        GameUIHandler.textFont.use();
        g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        g.textPaint(CONTENT_TEXT_COLOR);
        g.textSize(CONTENT_TEXT_SIZE);
        
        g.textBox(missionDescription, OFFSET, OFFSET, pageWidth - OFFSET * 2);
        int lines = g.textBoxLines(missionDescription, pageWidth - OFFSET * 2, 1000);
        
        if(!images.isEmpty()) {
            BookImage image = images.get(0);
            image.render(g, OFFSET, OFFSET + lines * CONTENT_TEXT_SIZE, pageWidth - OFFSET * 2);
        }
    }
    
    
    
}
