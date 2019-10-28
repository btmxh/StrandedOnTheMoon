/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer;

import codinggame.globjs.Camera;
import codinggame.handlers.MapHandler;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.cells.ColoredCell;
import codinggame.map.cells.FacingCell;
import codinggame.map.cells.renderers.CellRenderer;
import codinggame.map.cells.renderers.FacingCellRenderer;
import codinggame.map.cells.renderers.GlassRenderer;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.objs.buildings.Building;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;
import com.lwjglwrapper.opengl.objects.VAO;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javafx.util.Pair;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class MapRenderer {
    public static VAO VAO;
    protected CellRenderer defaultCellRenderer;
    protected FacingCellRenderer facingCellRenderer;
    protected GlassRenderer glassRenderer;
    
    public MapRenderer() {
        defaultCellRenderer = new CellRenderer();
        facingCellRenderer = new FacingCellRenderer();
        glassRenderer = new GlassRenderer();
        
        if(VAO == null) {
            VAO = new VAO();
            VAO.bind();
            VAO.createAttribute(0, new float[] {0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0}, 2);
            VAO.unbind();
        }
    }

    public void render(MapHandler mapHandler, GameMap map, Camera camera, boolean hover) {
        Vector2f cursorPosition = LWJGL.mouse.getCursorPosition();
        if(cursorPosition.x >= LWJGL.window.getViewport().getWidth()) {
            cursorPosition = null;
        }
        Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells = new HashMap<>();
        
        defaultCellRenderer.prepare();
        VAO.bindAll();
        for (int i = 0; i < map.getMapLayers().size(); i++) {
            MapLayer mapLayer = map.getMapLayers().get(i);
            renderLayer(mapHandler, mapLayer,(int) Math.ceil(LWJGL.window.getViewport().getWidth() / camera.getTileSize()),
                    (int) Math.ceil(LWJGL.window.getViewport().getHeight() / camera.getTileSize()), cursorPosition,
                    hover, i == map.getMapLayers().size() - 1, specialCells);
        }
        defaultCellRenderer.unbind();
        MapLayer topLayer = map.getMapLayers().get(map.getMapLayers().size() - 1);
        for (Map.Entry<CellRenderer, List<Pair<Point, ProcMapCell>>> entry : specialCells.entrySet()) {
            CellRenderer renderer = entry.getKey();
            renderer.prepare();
            List<Pair<Point, ProcMapCell>> pairs = entry.getValue();
            for (Pair<Point, ProcMapCell> pair : pairs) {
                Point pt = pair.getKey();
                ProcMapCell cell = pair.getValue();
                renderer.renderCell(mapHandler, topLayer, cell, pt.x, pt.y, cursorPosition, hover);
            }
            renderer.unbind();
        }
        VAO.unbindByLastBind();
    }
    
    public void renderLayer(MapHandler mapHandler, MapLayer layer, int width, int height, Vector2f cursorPosition, boolean hover, boolean topLayer, Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells) {
        int startX = -(int) (defaultCellRenderer.x / defaultCellRenderer.tileWidth) - width / 2 - 1;
        int startY = -(int) (defaultCellRenderer.y / defaultCellRenderer.tileHeight) - height / 2 - 1;
        
        for (int x = startX; x <= startX + width + 1; x++) {
            for (int y = startY; y <= startY + height + 1; y++) {
                MapCell cell = layer.getTileAt(x, y);
                renderCell(mapHandler, layer, x, y, cell, cursorPosition, hover, topLayer, specialCells);
            }
        }
    }
    
    public void renderCell(MapHandler mapHandler, Object layerOrBuilding, int x, int y, MapCell cell, Vector2f cursorPosition, boolean hover, boolean topLayer, Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells) {
        if(topLayer) {
            if(cell instanceof FacingCell) {
                putList(specialCells, LinkedList::new, facingCellRenderer, new Pair<>(new Point(x, y), (ProcMapCell) cell));
                return;
            } else if(cell instanceof ColoredCell) {
                putList(specialCells, LinkedList::new, glassRenderer, new Pair<>(new Point(x, y), (ProcMapCell) cell));
                return;
            }
        } 
        defaultCellRenderer.renderCell(mapHandler, layerOrBuilding, cell, x, y, cursorPosition, hover);
    }
    
    public void setTileSize(Viewport viewport, float tileWidth, float tileHeight) {
        defaultCellRenderer.setTileSize(viewport, tileWidth, tileHeight);
        facingCellRenderer.setTileSize(viewport, tileWidth, tileHeight);
        glassRenderer.setTileSize(viewport, tileWidth, tileHeight);
    }
    
    public void setOriginPosition(float x, float y) {
        defaultCellRenderer.setOriginPosition(x, y);
        facingCellRenderer.setOriginPosition(x, y);
        glassRenderer.setOriginPosition(x, y);
    }
    
    private static <K, V> void putList(Map<K, List<V>> map, Supplier<? extends List<V>> supplier, K key, V value) {
        List<V> list = map.get(key);
        if(list == null) {
            list = supplier.get();
            map.put(key, list);
        }
        list.add(value);
    } 
    
    public void renderBuilding(MapHandler handler, Building building, Camera camera) {
        Vector2f temp_cursorPosition = LWJGL.mouse.getCursorPosition();
        if(temp_cursorPosition.x >= LWJGL.window.getViewport().getWidth()) {
            temp_cursorPosition = null;
        }
        final Vector2f cursorPosition = temp_cursorPosition;
        Vector2f originPosition = new Vector2f(defaultCellRenderer.x, defaultCellRenderer.y);
        Vector2f newOriginPosition = new Vector2f(originPosition)
                .add(new Vector2f(building.getPosition()).mul(2f * camera.getTileSize() /LWJGL.window.getWidth(), 2f * camera.getTileSize() /LWJGL.window.getHeight()));
        Map<CellRenderer, List<Pair<Point, ProcMapCell>>> specialCells = new HashMap<>();
        setOriginPosition(newOriginPosition.x, newOriginPosition.y);
        VAO.bindAll();
        defaultCellRenderer.prepare();
        for (int x = 0; x < building.getWidth(); x++) {
            for (int y = 0; y < building.getHeight(); y++) {
                MapCell cell = building.getTileAt(x, y);
                renderCell(handler, building, x, y, cell, cursorPosition, true, true, specialCells);
            }
        }
        defaultCellRenderer.unbind();
        for (CellRenderer renderer : specialCells.keySet()) {
            renderer.prepare();
            specialCells.get(renderer).forEach((pair) -> {
                renderer.renderCell(handler, building, pair.getValue(), pair.getKey().x, pair.getKey().y, cursorPosition, true);
            });
            renderer.unbind();
        }
        VAO.unbindByLastBind();
    }
}
