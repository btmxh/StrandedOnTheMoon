/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.apis;

import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class RobotRadar extends GameObject{

    public RobotRadar(GameState game, Robot robot) {
        super(game, robot);
    }
    
    public int getTileAt(int x, int y) {
        MapLayer layer = game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER);
        
        if(layer instanceof ProcMapLayer) {
            EntityData data = ((ProcMapLayer) layer).getEntityAt(x, y);
            if(data != null? !data.isTileEntity():true) {
                return -1;
            }
            float distance = robot.get2DPosition().distance(x, y);
            waitFor(distance);
            return data.getTileID();
        }
        return -1;
    }
    
}
