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
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.robots.FarmingRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class PlantCommand extends Command{

    private MapCell seedCell;
    
    public PlantCommand(GameState game, CommandBlock parentCommandBlock,
            Robot executingRobot, CommandHandler executor, ItemType seed) {
        super(game, parentCommandBlock, executingRobot, executor);
        if(executingRobot instanceof FarmingRobot) {
            setMaxTime(2);
        } else {
            executor.throwExecutionError(this, "This robot is not a farming robot");
        }
        if((seedCell = seedCell(seed, game.getMapHandler().getMap().getTilesets())) == null) {
            executor.throwExecutionError(this, seed + " cannot be planted");
        }
    }
    
    @Override
    public void end() {
        Item item = executingRobot.getInventory().getItems().get(ItemTypes.POTATO_SEED_BAG);
        if(item == null)    executor.throwExecutionError(this, "Item not found");
        else {
            CountItem citem = (CountItem) item;
            if(citem.getAmount() < 1)   executor.throwExecutionError(this, "Item not found");
            citem.setAmount(citem.getAmount() - 1);
        }
        ProcMapLayer turfLayer = (ProcMapLayer) game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER);
        MapCell cell = turfLayer.getTileAt(executingRobot.getTileX(), executingRobot.getTileY());
        if(cell == null? true:cell.getTileID() == -1? true : cell.getTileID() != MapTile.SOIL) {
            executor.throwExecutionError(this, "This tile must be a soil tile to plant seeds");
        } else {
            turfLayer.setTileEntityAt(executingRobot.getTileX(), executingRobot.getTileY(), seedCell, null);
            super.end();
        }
    }

    @Override
    public void begin() {
        super.begin();
        if(!(executingRobot instanceof FarmingRobot)) {
            executor.throwExecutionError(this, "This robot is not a farming robot");
        }
    }

    @Override
    public void undoCommand() {
    }

    private MapCell seedCell(ItemType seed, MapTilesets tileset) {
        if(seed == ItemTypes.POTATO_SEED_BAG)    return ProcMapCell.createCell(MapTile.POTATO_CROPS);
        else return null;
    }
    
}
