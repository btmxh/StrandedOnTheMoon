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
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.cells.SheetCell;
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
            Robot executingRobot, CommandHandler executor, Item seed) {
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
        Item item = executingRobot.getInventory().getItems().get(ItemTypes.WHEAT_SEED_BAG);
        if(item == null)    executor.throwExecutionError(this, "Item not found");
        else {
            CountItem citem = (CountItem) item;
            if(citem.getAmount() < 1)   executor.throwExecutionError(this, "Item not found");
            citem.setAmount(citem.getAmount() - 1);
        }
        MapLayer turfLayer = game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER);
        MapLayer oreLayer = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER);
        MapCell cell = turfLayer.getTileAt(executingRobot.getTileX(), executingRobot.getTileY());
        if(cell == null? true:cell.getTileType() == null? true:cell.getTileType().getID() != MapTile.SOIL) {
            executor.throwExecutionError(this, "This tile must be a soil tile to plant seeds");
        } else {
            oreLayer.setTileAt(executingRobot.getTileX(), executingRobot.getTileY(), seedCell);
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

    private MapCell seedCell(Item seed, MapTilesets tileset) {
        ItemType type = seed.getItemType();
        if(type == ItemTypes.WHEAT_SEED_BAG)    return new SheetCell(tileset.getTileByID(MapTile.WHEAT_CROPS), 0);
        else return null;
    }
    
}
