/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.tiles;

import static codinggame.map.MapTile.*;
import codinggame.map.MapTile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class MapTiles {
    private static TileData[] tileData = new TileData[MapTile.LAST];
    
    static {
        put(new TileData(COPPER_ORE, "Copper Ore", true));
        put(new TileData(MOON_TURF, "Moon Turf", true));
        put(new TileData(CHARGER, "Charger", false));
        put(new TileData(BARRIER, "Barrier", true));
        put(new TileData(STORAGE_CELL, "Storage Cell", true));
        put(new TileData(MOON_ROCK, "Moon Rock", true));
        put(new TileData(CENTRAL_STORAGE_UNIT, "Central Storage Unit", true));
        put(new TileData(CHEMICAL_PROCESSOR, "Chemical Processor", true));
        put(new TileData(CONVEYOR, "Conveyor", false));
        put(new TileData(GLASS, "Glass", true));
        put(new TileData(SOIL, "Soil", true));
        put(new TileData(POTATO_CROPS, "Potato Crops", false));
        put(new TileData(ICE, "Ice", true));
        put(new TileData(IRON_ORE, "Iron Ore", true));
    }
    
    private static void put(TileData data) {
        tileData[data.id - 1] = data;
    }
    
    public static TileData get(int id) { 
        return tileData[id - 1];
    }
    
    public static class TileData {
        private String name;
        private boolean solid;
        private int id;

        public TileData(int id, String name, boolean solid) {
            this.name = name;
            this.solid = solid;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public boolean isSolid() {
            return solid;
        }
        
        
    }
}
