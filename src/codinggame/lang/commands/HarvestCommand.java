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
import codinggame.map.cells.SheetCell;
import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.util.List;

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
        MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).getTileAt(x, y);
        if(cell == null? true:cell.getTileType()==null) {
            executor.throwExecutionError(this, "Can not harvest!");
            return;
        } else {
            String tileName = cell.getTileType().getName();
            if(cell.getTileType().getID() == MapTile.POTATO_CROPS && ((SheetCell) cell).getIndex() == 3) {
                if(executingRobot.getInventory().add(new CountItem(ItemTypes.POTATO, 1))) {
                    game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).setTileAt(x, y, (MapTile) null);
                } else {
                    executor.throwExecutionError(this, "Not enough storage");
                }
            } else {
                executor.throwExecutionError(this, tileName + " is not harvestable");
            }
        }
        super.end();
    }
    
    

    @Override
    public void undoCommand() {
    }
    
}
