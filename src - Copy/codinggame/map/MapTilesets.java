/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

/**
 *
 * @author Welcome
 */
public class MapTilesets {
    public MapTileset[] tilesets;

    public MapTilesets(MapTileset[] tilesets) {
        this.tilesets = tilesets;
    }
    
    public MapTile getTileByID(int id) {
        for (MapTileset tileset : tilesets) {
            if(!tileset.containsTile(id))   continue;
            return tileset.getTile(id);
        }
        return null;
    }
}
