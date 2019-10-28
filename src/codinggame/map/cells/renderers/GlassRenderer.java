/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells.renderers;

import codinggame.handlers.MapHandler;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.cells.ColoredCell;
import static codinggame.map.renderer.MapRenderer.VAO;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec4;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.Logger;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

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
