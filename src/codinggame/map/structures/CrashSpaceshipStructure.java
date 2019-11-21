/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.structures;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.equipments.Drill;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class CrashSpaceshipStructure extends Structure{
    
    private static Map<Point, MapCell> tileMap;
    
    public CrashSpaceshipStructure(Vector2i origin, MapTilesets tileset) {
        super(origin, tileMap(tileset));
    }
    
    private static Map<Point, MapCell> tileMap(MapTilesets tileset) {
        if(tileMap != null) return tileMap;
        tileMap = new HashMap<>();
//        tileMap.put(new Point(0, 0), new ProcMapCell(tileset.getTileByID(MapTile.CHARGER)));
//        InventoryCell storageCell = new InventoryCell(tileset.getTileByID(MapTile.STORAGE_CELL));
//        storageCell.getInventory().add(new Drill(ItemTypes.TITANIUM_DRILL));
//        tileMap.put(new Point(0, 1), storageCell);
//        tileMap.put(new Point(0, 2), new SheetCell(tileset.getTileByID(MapTile.POTATO_CROPS), 3));
        
        return tileMap;
    }
    
}
