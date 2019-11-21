/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells.renderers;

import codinggame.handlers.MapHandler;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.cells.FacingCell;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec4;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class FacingCellRenderer extends CellRenderer{

    private UVec4 rotationMatrix;
    
    public FacingCellRenderer() {
        super("/codinggame/map/cells/renderers/facingcell/");
        rotationMatrix = new UVec4(mapShader, "rotationMatrix");
    }
    
    @Override
    public void renderCell(MapHandler mapHandler, Object layerOrBuilding, MapCell cell, int x,
            int y, Vector2f cursorPosition, boolean hover) {
        rotationMatrix.load(((FacingCell) cell).getFacing().getMatrix());
        super.renderCell(mapHandler, layerOrBuilding, cell, x, y, cursorPosition, hover);
    }
    
}
