/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells.renderers;

import codinggame.handlers.MapHandler;
import codinggame.map.MapCell;
import codinggame.map.cells.ColoredCell;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec4;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class GlassRenderer extends CellRenderer{

    private UVec4 tint;
    
    public GlassRenderer() {
        super("/codinggame/map/cells/renderers/glass/");
        tint = new UVec4(mapShader, "tint");
    }

    @Override
    public void renderCell(MapHandler mapHandler, Object layerOrBuilding,
            MapCell cell, int x, int y, Vector2f cursorPosition, boolean hover) {
        tint.load(((ColoredCell) cell).getColor().to4DVector());
        super.renderCell(mapHandler, layerOrBuilding, cell, x, y, cursorPosition, hover);
    }

    
}
