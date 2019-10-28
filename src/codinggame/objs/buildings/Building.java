/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.buildings;

import codinggame.map.MapCell;
import codinggame.map.MapTileset;
import codinggame.map.MapTilesets;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class Building {
    protected Vector2f position;
    protected int width, height;
    protected MapCell[][] cells;

    public Building(Vector2f position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
        cells = new MapCell[width][height];
    }
    
    public void setTiles(MapTilesets tilesets) {}
    
    public void setTiles(MapTileset tileset) {
        setTiles(new MapTilesets(new MapTileset[]{tileset}));
    }
    
    public void setTileAt(int x, int y, MapCell cell) {
        cells[x][y] = cell;
    }
    
    public MapCell getTileAt(int x, int y) {
        return cells[x][y];
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    
    
}
