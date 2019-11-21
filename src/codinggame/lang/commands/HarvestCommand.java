/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.commands;

import codinggame.handlers.CommandHandler;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.map.GameMap;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.tiles.MapTiles;
import codinggame.objs.items.CountItem;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class HarvestCommand extends Command{

    public HarvestCommand(GameState game, CommandBlock parentCommandBlock,
            Robot executingRobot, CommandHandler executor) {
        super(game, parentCommandBlock, executingRobot, executor);
        setMaxTime(1f);
    }

    @Override
    public void end() {
        int x = executingRobot.getTileX();
        int y = executingRobot.getTileY();
        MapLayer layer = game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER);
        if(!(layer instanceof ProcMapLayer))    throw new UnsupportedOperationException();
        ProcMapLayer turfLayer = (ProcMapLayer) layer;
        EntityData data = turfLayer.getEntityAt(x, y);
        if(data == null? true:!data.isTileEntity()) {
            executor.throwExecutionError(this, "Can not harvest!");
            return;
        }
        String tileName = MapTiles.get(data.getTileID()).getName();
        if(data.getTileID() == MapTile.POTATO_CROPS && data.getTagID() == 3) {
            if(executingRobot.getInventory().add(new CountItem(ItemTypes.POTATO, 1))) {
                turfLayer.setTileEntityAt(x, y, null, null);
            } else {
                executor.throwExecutionError(this, "Not enough storage");
                return;
            }
        } else {
            executor.throwExecutionError(this, tileName + " is not harvestable");
            return;
        }
        super.end();
    }
    
    

    @Override
    public void undoCommand() {
    }
    
}
