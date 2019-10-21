/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.map.tiledmap.*;

/**
 *
 * @author Welcome
 */
public interface MapLayer {

    public void setTileAt(int x, int y, MapTile tile);
    public void setTileAt(int x, int y, MapCell cell);
    public MapCell getTileAt(int x, int y);
    void setID(int id);
    int getID();
}
