/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells.renderers;

import codinggame.handlers.MapHandler;
import codinggame.map.MapCell;
import codinggame.map.cells.SheetCell;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec2;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec4;
import java.text.DecimalFormat;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 *
 * @author Welcome
 */
public class SheetRenderer extends CellRenderer{

    private final UVec2 offset;
    private final UInt noOfRows;
    
    public SheetRenderer() {
        super("/codinggame/map/cells/renderers/sheets/");
        offset = new UVec2(mapShader, "offset");
        noOfRows = new UInt(mapShader, "noOfRows");
    }

    @Override
    public void renderCell(MapHandler mapHandler, Object layerOrBuilding,
            MapCell cell, int x, int y, Vector2f cursorPosition, boolean hover) {
        SheetCell sheetCell = (SheetCell) cell;
        noOfRows.load(sheetCell.getTileType().getNumberOfRows());
        offset.load(sheetCell.getTileType().getOffset(sheetCell.getIndex()));
        super.renderCell(mapHandler, layerOrBuilding, cell, x, y, cursorPosition, hover); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
