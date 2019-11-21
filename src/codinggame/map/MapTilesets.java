/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Welcome
 */
public class MapTilesets {
    public MapTileset[] tilesets;

    public MapTilesets(MapTileset[] tilesets) {
        this.tilesets = tilesets;
    }
    
    public <T extends MapTile> T getTileByID(int id) {
        for (MapTileset tileset : tilesets) {
            if(!tileset.containsTile(id))   continue;
            return (T) tileset.getTile(id);
        }
        return null;
    }

    public Map<Integer, MapTile> getTiles() {
        return Stream.of(tilesets).map(MapTileset::getTiles).map(Map::entrySet).flatMap(Set::stream).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
