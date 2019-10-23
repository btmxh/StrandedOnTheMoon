/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import com.lwjglwrapper.nanovg.paint.Paint;
import com.lwjglwrapper.utils.geom.shapes.GLRect;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Stage;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class IButton extends Button {
    
    private String text = "";
    
    public IButton(Stage stage, boolean autoAdd, GLRect rect, Paint[] fills, Paint stroke, Paint textPaint) {
        super(stage, autoAdd);
        shapes.reset().setAll(new Rect(rect))
                      .setAllStrokes(stroke)
                      .setFill(fills[NORMAL], NORMAL)
                      .setFill(fills[HOVER], HOVER)
                      .setFill(fills[CLICKED], CLICKED)
                      .setAllAfterPaints((g) -> {
                          CodingArea.textFont.use();
                          g.beginPath();
                          g.textAlign(NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
                          g.textSize(rect.getHeight().get() * 0.5f);
                          g.textPaint(textPaint);
                          g.text(text, rect.centerX().get(), rect.centerY().get());
                      }).construct(false);
                
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    
    
}
