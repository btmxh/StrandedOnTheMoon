/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import codinggame.handlers.GameUIHandler;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.colors.StaticColor;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.math.MathUtils;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.Panel;
import com.lwjglwrapper.utils.ui.Stage;
import java.util.ArrayList;
import java.util.List;
import org.joml.Rectanglef;
import org.lwjgl.nanovg.NanoVG;

/**
 * 
 * @author Welcome
 */
public abstract class Tab extends Panel {

    public static final int NORMAL = 0, HOVER = 1, CLICKED = 2;

    private String text;

    private boolean open = false;
    private final Button button;
    private Rect buttonBounds;

    public Tab(Stage stage, String text, Rect buttonBounds, boolean autoAdd) {
        super(stage, autoAdd);
        this.text = text;
        this.buttonBounds = buttonBounds;
        
        shapes.reset(); //No Shapes

        button = new Button(stage, false);
        button.getShapeStates().reset().setAll(buttonBounds)
                .setAllStrokes(StaticColor.BLACK)
                .setFill(StaticColor.GRAY, NORMAL)
                .setFill(StaticColor.LIGHTGRAY, HOVER)
                .setFill(StaticColor.DARKGRAY, CLICKED)
                .setAllAfterPaints((g) -> {
                    Rectanglef rect = buttonBounds.getJOMLRect();
                    final float WIDTH = rect.maxX - rect.minX, HEIGHT = rect.maxY - rect.minY;
                    final float TRIANGLE_SIZE = 10, TEXT_SIZE = HEIGHT / 1.5f;
                    g.setUpText(GameUIHandler.textFont, StaticColor.WHITE, TEXT_SIZE, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(this.text, 10, HEIGHT / 2);
                    float[] ys;
                    if (open) {
                        ys = new float[]{HEIGHT / 2 + TRIANGLE_SIZE / 2, HEIGHT / 2 + TRIANGLE_SIZE / 2, HEIGHT / 2 - TRIANGLE_SIZE / 2};
                    } else {
                        ys = new float[]{HEIGHT / 2 - TRIANGLE_SIZE / 2, HEIGHT / 2 - TRIANGLE_SIZE / 2, HEIGHT / 2 + TRIANGLE_SIZE / 2};
                    }
                    g.polygon(new float[]{
                        WIDTH - TRIANGLE_SIZE * 2, WIDTH - TRIANGLE_SIZE, WIDTH - TRIANGLE_SIZE * 1.5f
                    }, ys, 3);
                    g.fill(StaticColor.WHITE);
                    g.translate(0, HEIGHT);
                }).construct(false);
        button.setOnClickListener((s, b, m) -> {
            open = !open;
        });
    }

    @Override
    public void tick() {
        if(!isVisible())    return;
        super.tick();
        button.tick();
    }
    
    
    
    @Override
    public void render(NVGGraphics g) {
        if(!isVisible())    return;
        button.render(g);
        for (Component comp : children) {
            comp.render(g);
        }
        if(open) {
            renderContent(g);
        }
    }

    public abstract void renderContent(NVGGraphics g);

    @Override
    public Component findHover() {
        Component hover = super.findHover();
        if(button.isVisible() && button.getShapeStates() != null && button.getCurrentShape() != null) {
            if(MathUtils.contains(button.getCurrentShape().getShape().boundBox(), button.detransformedMousePosition)) {
//                System.out.println("codinggame.ui.Tab.findHover()");
                return button;
            }
        }
        return hover;
    }

    @Override
    public List<Component> children() {
        ArrayList<Component> children = new ArrayList<>(super.children());
        children.add(button);
        return children;
    }
    
    
    
    
}
