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
import codinggame.map.MapTile;
import codinggame.map.cells.ColoredCell;
import codinggame.map.cells.FacingCell;
import codinggame.map.renderer.MapRenderer;
import static codinggame.map.renderer.MapRenderer.VAO;
import codinggame.objs.buildings.Building;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec2;
import com.lwjglwrapper.utils.colors.StaticColor;
import java.text.DecimalFormat;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class CellRenderer {
    
    protected Shader mapShader;
    protected UVec2 umtranslation, umscale;
    protected UInt umtexture, umhover, umbreakMap, umbreakStage;
    protected Texture2D breakMap;
    
    public float tileWidth, tileHeight;
    public float x, y;

    public CellRenderer() {
        this("/codinggame/map/renderer/");
    }
    
    public CellRenderer(String shaderpath) {
        init(shaderpath);
    }
    
    public void init(String shaderpath) {
        mapShader = new Shader(
            ShaderFile.fromResource(MapRenderer.class, shaderpath + "vertex.glsl", GL20.GL_VERTEX_SHADER),
            ShaderFile.fromResource(MapRenderer.class, shaderpath + "fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        
        umtranslation = new UVec2(mapShader, "translation");
        umscale = new UVec2(mapShader, "scale");
        umhover = new UInt(mapShader, "hoveringType");
        umbreakMap = new UInt(mapShader, "breakMap");
        umtexture = new UInt(mapShader, "tileTexture");
        umbreakStage = new UInt(mapShader, "breakStage");
        
        mapShader.bind();
        umtexture.load(0);
        umbreakMap.load(1);
        mapShader.unbind();
        
        breakMap = new Texture2D(TextureData.fromResource(MapRenderer.class, "/breakSpritesheet.png"));
    }
    
    public void prepare() {
        mapShader.bind();
        breakMap.bind(1);
    }
    
    public void renderCell(MapHandler mapHandler, Object layerOrBuilding, MapCell cell, int x, int y, Vector2f cursorPosition, boolean hover) {
        if(!hover)  cursorPosition = null;
        if(cell == null)    return;
        MapTile tile = cell.getTileType();
        if(tile == null)    return;
        if(tile.getTexture() == null)   return;
        float renderX = this.x + x * tileWidth;
        float renderY = this.y + y * tileHeight;
        umtranslation.load(renderX, renderY);
        tile.getTexture().bind(0);
        if(cursorPosition == null) {
            umhover.load(0);
        } else {
            float ndcx = cursorPosition.x / LWJGL.window.getViewport().getWidth() * 2 - 1;
            float ndcy = 1 - cursorPosition.y / LWJGL.window.getViewport().getHeight() * 2;
            float dx = ndcx - renderX;
            float dy = ndcy - renderY;
            if(dx > 0 && dx < tileWidth && dy > 0 && dy < tileHeight) {
                umhover.load(1);
                if(mapHandler != null) {
                    if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                        mapHandler.chooseTile(layerOrBuilding, x, y);
                    } else if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                        //Testing
                        mapHandler.getMap().getMapLayer(GameMap.ORE_LAYER).setTileAt(x, y, new ColoredCell(StaticColor.RED, mapHandler.getMap().getTilesets().getTileByID(MapTile.GLASS)));
                    }
                }
            } else umhover.load(0);
        }
        if(mapHandler != null? mapHandler.tileChoosen(layerOrBuilding, x, y):false) {
            umhover.load(2);
        }
        umbreakStage.load(cell.getBreakingStage());
        VAO.renderArray(GL11.GL_TRIANGLES, 0, -1);
    }
    
    public void unbind() {
        mapShader.unbind();
        breakMap.unbind();
    }
    
    public void dispose() {
        mapShader.dispose();
        breakMap.dispose();
    }
    
    public void setTileSize(Viewport viewport, float tileWidth, float tileHeight) {
        this.tileWidth = tileWidth / viewport.getWidth() * 2;
        this.tileHeight = tileHeight / viewport.getHeight() * 2;
        mapShader.bind();
        umscale.load(this.tileWidth, this.tileHeight);
        mapShader.unbind();
    }

    public void setOriginPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
}
