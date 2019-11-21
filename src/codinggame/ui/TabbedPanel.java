/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import aurelienribon.tweenengine.TweenAccessor;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.math.MathUtils;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.ComponentCollection;
import com.lwjglwrapper.utils.ui.Panel;
import com.lwjglwrapper.utils.ui.Stage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class TabbedPanel extends ComponentCollection{
    
    public float alpha = 1;
    public float dx = 0;
    
    private List<Panel> panels;
    private List<Component> components;
    private int selected = -1;

    public TabbedPanel(Stage stage, boolean autoAdd) {
        super(stage, autoAdd, 1);
        this.panels = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public List<Panel> getPanels() {
        return panels;
    }

    @Override
    public void render(NVGGraphics g) {
        g.push();
        g.translate(dx, 0);
        g.pushAlpha();
        g.mulAlpha(alpha);
        if(selected >= 0 && selected < panels.size()) {
            panels.get(selected).render(g);
        }
        if(selected >= 0) {
            components.forEach(c -> c.render(g));
        }
        g.popAlpha();
        g.pop();
    }

    @Override
    public void tick() {
        if(selected >= 0 && selected < panels.size()) {
            panels.get(selected).tick();
        }
        for (Component comp : components) {
            comp.tick();
        }
    }
    
    public void select(int idx) {
        selected = idx;
    }

    @Override
    public Component findHover() {
        Component hovering = null;
        for(int i = components.size() - 1; i >= 0; i--) {
            Component comp = components.get(i);
            if(!comp.isVisible() || hovering != null)   continue;
            if(comp instanceof Panel)    hovering = ((Panel) comp).findHover();
            if(comp.getCurrentShape() == null)   continue;
            boolean hover = MathUtils.contains(comp.getCurrentShape().getShape().boundBox(), comp.detransformedMousePosition);
            if(hover)   hovering = comp;
        }
        if(hovering != null)    return hovering;
        if(selected >= 0 && selected < panels.size()) {
            return panels.get(selected).findHover();
        }
        return null;
    }

    public int getSelectedIndex() {
        return selected;
    }

    @Override
    public List<Panel> children() {
        return panels;
    }

    public List<Component> getComponents() {
        return components;
    }
    
    public static class Accessor implements TweenAccessor<TabbedPanel> {

        public static final int FADE = 0, MOVE = 1;
        
        @Override
        public int getValues(TabbedPanel target, int tweenType,
                float[] returnValues) {
            switch (tweenType) {
                case FADE:  returnValues[0] =  target.alpha;    return 1;
                case MOVE:  returnValues[0] =  target.dx;    return 1;
            }
            return -1;
        }

        @Override
        public void setValues(TabbedPanel target, int tweenType,
                float[] newValues) {
            switch (tweenType) {
                case FADE:  target.alpha = newValues[0];    break;
                case MOVE:  target.dx = newValues[0];       break;
            }
        }
        
    }    
    
}
