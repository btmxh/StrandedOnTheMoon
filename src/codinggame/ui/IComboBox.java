/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import com.lwjglwrapper.nanovg.NVGFont;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;
import com.lwjglwrapper.utils.geom.shapes.GLRect;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.geom.shapes.RoundRect;
import com.lwjglwrapper.utils.ui.ComboBox;
import static com.lwjglwrapper.utils.ui.ComboBox.ComboBoxCell.CLICKED;
import static com.lwjglwrapper.utils.ui.ComboBox.ComboBoxCell.HOVER;
import static com.lwjglwrapper.utils.ui.ComboBox.ComboBoxCell.NORMAL;
import com.lwjglwrapper.utils.ui.Stage;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class IComboBox extends ComboBox{
    
    private GLRect bounds;
    private NVGFont textFont;
    
    private static final IColor COLOR = IColor.GRAY;
    private static final IColor HOVER_COLOR = new IColor(0.82f);
    
    private static final float BAR_WIDTH = 20, TEXT_OFFSET = 4, CELL_OFFSET = 5;
    
    public IComboBox(Stage stage, float cellHeight, GLRect bounds, NVGFont textFont) {
        super(stage, false, bounds.getX(), bounds.getMaxY(), cellHeight);
        this.bounds = bounds;
        this.textFont = textFont;
        shapes.reset().set(new PaintedShape(normalShape(), null, null), NORMAL)
                      .set(new PaintedShape(hoverShape(), null, null), HOVER)
                      .set(new PaintedShape(clickedShape(), null, null), CLICKED);
    }

    private Shape normalShape() {
        return createShape(COLOR, new IColor(0.4f), new IColor(0.2f));
    }

    private Shape hoverShape() {
        return createShape(HOVER_COLOR, new IColor(0.4f), new IColor(0.2f));
    }

    private Shape clickedShape() {
        return createShape(COLOR.darker(), new IColor(0.4f), new IColor(0.2f));
    }
    
    private Shape createShape(IColor color, IColor barColor, IColor arrowColor) {
        return new Shape() {
            @Override
            public Rectanglef boundBox() {
                return bounds.getJOMLRect();
            }
            float f = 0;
            @Override
            public void render(NVGGraphics g) {
                new RoundRect(bounds, ()->2).render(g);
                g.fill(color);
                g.roundRect(bounds.getMaxX().get() - BAR_WIDTH, bounds.getY().get(), BAR_WIDTH + TEXT_OFFSET, bounds.getHeight().get(), 0, 2, 2, 0);
                g.fill(barColor);
                float x1 = bounds.getMaxX().get() - BAR_WIDTH / 4, x2 = bounds.getMaxX().get() - BAR_WIDTH * 3 / 4, x3 = bounds.getMaxX().get() - BAR_WIDTH * 2 / 4;
                float y1 = bounds.getY().get() + (bounds.getHeight().get() - BAR_WIDTH / 2) / 2, //y1 == y2
                      y3 = bounds.getMaxY().get() - bounds.getHeight().get() / 4;
                
                g.triangle(x1, y1, x2, y1, x3, y3);
                g.fill(arrowColor);
                
                if(cells.isEmpty() | selectedIndex < 0) return;
                
                textFont.use();
                
                g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
                g.textSize(bounds.getHeight().get() * 0.7f);
                g.textPaint(IColor.WHITE);
                g.text(((IComboBoxCell) cells.get(selectedIndex)).content, bounds.getX().get() + CELL_OFFSET + TEXT_OFFSET, bounds.centerY().get() + 2);
            }
        };
    }

    public void setBounds(GLRect bounds) {
        this.bounds = bounds;
        this.cellCornerX = bounds.getX();
        this.cellCornerY = bounds.getMaxY();
    }

    public void set(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
    
    public static class IComboBoxCell extends ComboBoxCell {
        
        private String content;
        private static final IColor HOVER_COLOR = new IColor(0.23f, 0.60f, 0.86f);
        
        public IComboBoxCell(ComboBox comboBox, String content) {
            super(comboBox, true);
            this.content = content;
            
            shapes.set(new PaintedShape(normalShape(), null, null), NORMAL)
                  .set(new PaintedShape(hoverShape(), null, null), HOVER)
                  .set(new PaintedShape(clickedShape(), null, null), CLICKED);
        }
        
        public Shape normalShape() {
            return createShape(COLOR);
        }
        public Shape hoverShape() {
            return createShape(HOVER_COLOR);
        }
        public Shape clickedShape() {
            return createShape(COLOR.darker());
        }
        
        private Shape createShape(IColor color) {
            return new Shape() {
                @Override
                public Rectanglef boundBox() {
                    IComboBox comboBox = (IComboBox) IComboBoxCell.this.comboBox;
                    return Rect.jomlRect(TEXT_OFFSET, 0, comboBox.bounds.getWidth().get() - BAR_WIDTH, comboBox.getCellHeight());
                }

                @Override
                public void render(NVGGraphics g) {
                    IComboBox comboBox = (IComboBox) IComboBoxCell.this.comboBox;
                    g.rect(TEXT_OFFSET, 0, comboBox.bounds.getWidth().get() - BAR_WIDTH, comboBox.getCellHeight());
                    g.fill(color);
                    
                    comboBox.textFont.use();
                    g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
                    g.textSize(comboBox.bounds.getHeight().get() * 0.7f);
                    g.textPaint(IColor.WHITE);
                    g.text(content, CELL_OFFSET + TEXT_OFFSET, comboBox.getCellHeight() / 2);
                    
                }
            };
        }

        @Override
        public void render(NVGGraphics g) {
            super.render(g);
        }

        @Override
        public void tick() {
            super.tick();
        }
        
        
        
        
        
    }
    
}
