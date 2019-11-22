/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.CellUtils;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.states.GameState;
import org.joml.Vector2f;
import static codinggame.handlers.TileUpdateHandler.Speed.*;
import codinggame.map.proceduralmap.ProcMapCell;

/**
 *
 * @author Welcome
 */
public class TileUpdateHandler {

//    private static final float CELL_UPDATE_SPEED = 8f;
    
    public static final class Speed {
        public static final float POTATO_GROW_SPEED = 0.3f;
        public static final float HYDRATION_LOST_SPEED = 0.3f;
        
    }
    

    private GameState game;
    private MapHandler mapHandler;

    public TileUpdateHandler(GameState game, MapHandler mapHandler) {
        this.game = game;
        this.mapHandler = mapHandler;
    }

    public <T> void update(ProcMapChunk chunk) {
//        float tilesize = game.getCamera().getTileSize();
//        int tileFitScreen = (int) (Math.max(LWJGL.window.getWidth(), LWJGL.window.getHeight()) / tilesize);
//        tileFitScreen = MathUtils.clamp(30, tileFitScreen, 200);
//        for (MapLayer layer : mapHandler.getMap().getMapLayers()) {
//            updateLayer(layer, game.getCamera().getCenteredTile(), tileFitScreen);
//        }
    }

    private void updateLayer(MapLayer layer, Vector2f centeredTile, int updateRadius) {
//        Utils.random(CELL_UPDATE_SPEED * LWJGL.currentLoop.getDeltaTime(), () -> {
//            double r = Math.random() * updateRadius;
//            double ang = Math.random() * Math.PI;
//            int tx = (int) (centeredTile.x + r * Math.cos(ang));
//            int ty = (int) (centeredTile.y + r * Math.sin(ang));
//            MapCell cell = layer.getTileAt(tx, ty);
//            updateCell(cell);
//        });
    }

    public static void updateCell(ProcMapCell cell) {
        if(!MapCell.nullCheck(cell))    return;
        
        int tileID = cell.getTileID();
        switch (tileID) {
            case MapTile.SOIL:
                float hydration = CellUtils.soil_getHydration(cell);
                if(Math.random() < HYDRATION_LOST_SPEED)  CellUtils.soil_setHydration(cell, Math.max(0f, hydration - 0.01f));
        }
    }
    
    public static void updateEntityCell(EntityData cellData) {
        if(!cellData.isTileEntity())    return;
        int tileID = cellData.getTileID();
        switch (tileID) {
            case MapTile.POTATO_CROPS:
                int stage = cellData.getTagID();
                if(stage < 3 && Math.random() < POTATO_GROW_SPEED)   cellData.setTagID(stage + 1);
                return;
        }
    }

}
