/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.handlers.ObjectChooseHandler;

/**
 *
 * @author Welcome
 */
public interface MapCell extends ObjectChooseHandler.Choosable{

    public static boolean nullCheck(MapCell cell) {
        return cell != null? cell.getTileID() != -1:false;
    }

    public int getTileID();
    public int getBreakingStage();
    public void increaseBreakingProgress(float deltabp);
    public boolean isBreaking();
    
    public default MapTile getTileType(MapTileset tileset) {
        int id;
        if((id = getTileID()) == -1)   return null;
        return tileset.getTile(id);
    }
    
}
