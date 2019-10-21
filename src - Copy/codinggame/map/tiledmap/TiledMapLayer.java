/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.tiledmap;

import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;

/**
 *
 * @author Welcome
 */
public class TiledMapLayer implements MapLayer{

    private int id;
    private int width, height;
    private TiledMapCell[][] map;
    
    TiledMapLayer(int width, int height) {
        this.width = width;
        this.height = height;
        map = new TiledMapCell[width][height];
    }
    
    @Override
    public void setTileAt(int x, int y, MapTile tile) {
        if(x < 0 | x >= width | y < 0 | y >= height)    return;
        if(map[x][y] == null)   setTileAt(x, y, new TiledMapCell(tile));
        else map[x][y].setTileType(tile);
    }
    
    @Override
    public void setTileAt(int x, int y, MapCell cell) {
        if(x < 0 | x >= width | y < 0 | y >= height)    return;
        map[x][y] = (TiledMapCell) cell;
    }
    
    @Override
    public TiledMapCell getTileAt(int x, int y) {
        if(x < 0 | x >= width | y < 0 | y >= height)    return null;
        return map[x][y];
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }
}
