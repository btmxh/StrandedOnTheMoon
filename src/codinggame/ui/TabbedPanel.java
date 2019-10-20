/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import com.lwjglwrapper.nanovg.NVGGraphics;
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
    
    private List<Panel> panels;
    private int selected = 0;

    public TabbedPanel(Stage stage, boolean autoAdd) {
        super(stage, autoAdd, 1);
        this.panels = new ArrayList<>();
    }

    public List<Panel> getPanels() {
        return panels;
    }

    @Override
    public void render(NVGGraphics g) {
        if(selected >= 0 && selected < panels.size()) {
            panels.get(selected).render(g);
        }
    }

    @Override
    public void tick() {
        if(selected >= 0 && selected < panels.size()) {
            panels.get(selected).tick();
        }
    }
    
    public void select(int idx) {
        selected = idx;
    }

    @Override
    public Component findHover() {
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
    
    
    
    
}
