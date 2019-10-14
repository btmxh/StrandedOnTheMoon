/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.CodingGame;

/**
 *
 * @author Welcome
 */
public class GameMap {
    public static final int ORE_LAYER = 2, TURF_LAYER = 1;
    
    private MapTilesets tilesets;
    private MapLayers mapLayers;

    public GameMap(MapTilesets tilesets, MapLayers mapLayers) {
        this.tilesets = tilesets;
        this.mapLayers = mapLayers;
    }

    /**
     * @return the tilesets
     */
    public MapTilesets getTilesets() {
        return tilesets;
    }

    public MapLayer getMapLayer(int id) {
        for (MapLayer mapLayer : mapLayers) {
            if(mapLayer.getID() == id) {
                return mapLayer;
            } 
        }
        return null;
    }

    public MapLayers getMapLayers() {
        return mapLayers;
    }

    //Choosing
    public static class ChooseTile {
        public final int x, y;
        public final MapLayer layer;

        public ChooseTile(int x, int y, MapLayer layer) {
            this.x = x;
            this.y = y;
            this.layer = layer;
        }
        
    }
    
    public ChooseTile chooseTile;
    
    public void chooseTile(MapLayer layer, int x, int y) {
        chooseTile = new ChooseTile(x, y, layer);
        CodingGame.getInstance().getGameState().select(chooseTile);
    }
    
    public boolean tileChoosen(MapLayer layer, int x, int y) {
        if(CodingGame.getInstance().getGameState().getSelectedObject() instanceof ChooseTile) 
            return chooseTile == null? false:chooseTile.x == x && chooseTile.y == y && chooseTile.layer == layer;
        else return false;
    }
    
}
