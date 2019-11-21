/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.states.GameState;
import codinggame.utils.Utils;
import com.lwjglwrapper.LWJGL;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class TileUpdateHandler {

//    private static final float CELL_UPDATE_SPEED = 8f;
    private static final float CELL_UPDATE_SPEED = 50f;
    

    private GameState game;
    private MapHandler mapHandler;

    public TileUpdateHandler(GameState game, MapHandler mapHandler) {
        this.game = game;
        this.mapHandler = mapHandler;
    }

    public void update() {
//        float tilesize = game.getCamera().getTileSize();
//        int tileFitScreen = (int) (Math.max(LWJGL.window.getWidth(), LWJGL.window.getHeight()) / tilesize);
//        tileFitScreen = MathUtils.clamp(30, tileFitScreen, 200);
//        for (MapLayer layer : mapHandler.getMap().getMapLayers()) {
//            updateLayer(layer, game.getCamera().getCenteredTile(), tileFitScreen);
//        }
    }

    private void updateLayer(MapLayer layer, Vector2f centeredTile, int updateRadius) {
        Utils.random(CELL_UPDATE_SPEED * LWJGL.currentLoop.getDeltaTime(), () -> {
            double r = Math.random() * updateRadius;
            double ang = Math.random() * Math.PI;
            int tx = (int) (centeredTile.x + r * Math.cos(ang));
            int ty = (int) (centeredTile.y + r * Math.sin(ang));
            MapCell cell = layer.getTileAt(tx, ty);
            updateCell(cell);
        });
    }

    private void updateCell(MapCell cell) {
        if(!MapCell.nullCheck(cell))    return;
        
        if (cell.getTileID() == MapTile.POTATO_CROPS) {
//            SheetCell sheetCell = (SheetCell) cell;
//            if (sheetCell.getIndex() < 3) {
//                sheetCell.setIndex(sheetCell.getIndex() + 1);
//            }
        }
    }

}
