/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.map.tiledmap.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class MapTileset {
    
    private Map<Integer, MapTile> tiles = new HashMap<>();

    public MapTileset() {
    }
    
    public void addTile(MapTile tile) {
        tiles.put(tile.getID(), tile);
    }
    
    public MapTile getTile(int id) {
        return tiles.get(id);
    }

    public boolean containsTile(int id) {
        return tiles.containsKey(id);
    }
}
