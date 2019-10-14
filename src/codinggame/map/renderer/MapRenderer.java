/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer;

import codinggame.globjs.Camera;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMapCell;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec2;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class MapRenderer {
    private Shader mapShader;
    private UVec2 umtranslation, umscale;
    private UInt umtexture, umhover, umbreakMap, umbreakStage;
    private VAO vao;
    private Texture2D breakMap;
    
    private float tileWidth, tileHeight;
    private float x, y;
    
    public MapRenderer() {
        mapShader = new Shader(
                ShaderFile.fromResource(MapRenderer.class, "vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(MapRenderer.class, "fragment.glsl", GL20.GL_FRAGMENT_SHADER)
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
        
        vao = new VAO();
        vao.bind();
        vao.createAttribute(0, new float[] {0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0}, 2);
        vao.unbind();
    }

    public void render(GameMap map, Camera camera, boolean hover) {
        Vector2f cursorPosition = LWJGL.mouse.getCursorPosition();
        if(cursorPosition.x >= LWJGL.window.getViewport().getWidth()) {
            cursorPosition = null;
        }
        
        mapShader.bind();
        vao.bindAll();
        breakMap.bind(1);
        for (int i = 0; i < map.getMapLayers().size(); i++) {
            MapLayer mapLayer = map.getMapLayers().get(i);
            renderLayer(map, mapLayer,(int) Math.ceil(LWJGL.window.getViewport().getWidth() / camera.getTileSize()),
                    (int) Math.ceil(LWJGL.window.getViewport().getHeight() / camera.getTileSize()), cursorPosition,
                    hover);
        }
        vao.unbindByLastBind();
        mapShader.unbind();
    }
    
    public void renderLayer(GameMap map, MapLayer layer, int width, int height, Vector2f cursorPosition, boolean hover) {
        int startX = -(int) (this.x / this.tileWidth) - width / 2 - 1;
        int startY = -(int) (this.y / this.tileHeight) - height / 2 - 1;
        List<Pair<Integer, Integer>> breakingTiles = new ArrayList<>();
        
        for (int x = startX; x <= startX + width + 1; x++) {
            for (int y = startY; y <= startY + height + 1; y++) {
                MapCell tile = layer.getTileAt(x, y);
                renderTile(map, layer, tile, x, y, breakingTiles, cursorPosition, hover);
            }
        }
//        for (Pair<Integer, Integer> pos : breakingTiles) {
//            MapCell cell = layer.getTileAt(pos.getKey(), pos.getValue());
//            GLCalls.enable(GL11.GL_BLEND);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            
//            final int 
//        }
    }
    
    public void renderTile(GameMap map, MapLayer layer, MapCell cell, int x, int y, List<Pair<Integer, Integer>> breakingTiles, Vector2f cursorPosition, boolean hover) {
        if(!hover)  cursorPosition = null;
        if(cell == null)    return;
        MapTile tile = cell.getTileType();
        if(tile == null)    return;
        if(tile.getTexture() == null)   return;
        //if(cell.isBreaking())   breakingTiles.add(new Pair<>(x, y));
        float renderX = this.x + x * tileWidth;
        float renderY = this.y + y * tileHeight;
        umtranslation.load(renderX, renderY);
        tile.getTexture().bind(0);
//        System.out.println(renderX + " " + renderY + " " + tileWidth + " " + tileHeight);
        if(cursorPosition == null) {
            umhover.load(0);
        } else {
            float ndcx = cursorPosition.x / LWJGL.window.getViewport().getWidth() * 2 - 1;
            float ndcy = 1 - cursorPosition.y / LWJGL.window.getViewport().getHeight() * 2;
            float dx = ndcx - renderX;
            float dy = ndcy - renderY;
            if(dx > 0 && dx < tileWidth && dy > 0 && dy < tileHeight) {
                umhover.load(1);
                if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    map.chooseTile(layer, x, y);
                }
            } else umhover.load(0);
        }
        if(map.tileChoosen(layer, x, y)) {
            umhover.load(2);
        }
        umbreakStage.load(cell.getBreakingStage());
        vao.renderArray(GL11.GL_TRIANGLES, 0, -1);
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

    public String getOriginPosition() {
        return x + " " + y;
    }
}
