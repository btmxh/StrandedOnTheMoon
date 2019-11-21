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
import codinggame.objs.items.Item;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.items.equipments.Hoe;
import codinggame.objs.robots.FarmingRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class HoeCommand extends Command{

    public HoeCommand(GameState game, CommandBlock parentCommandBlock,
            Robot executingRobot, CommandHandler executor) {
        super(game, parentCommandBlock, executingRobot, executor);
        
        if(executingRobot instanceof FarmingRobot) {
            Hoe hoe = (Hoe) executingRobot.getInventory().getEquipment(EquipmentSlot.HOE);
            float speed = hoe.getItemType().getSpeed();
            setMaxTime(2 / speed);
            System.out.println(2 / speed);
        } else {
            executor.throwExecutionError(this, "This robot is not a farming robot");
        }
    }

    @Override
    public void end() {
        MapLayer layer = game.getMapHandler().getMap().getMapLayer(GameMap.TURF_LAYER);
        MapCell cell = layer.getTileAt(executingRobot.getTileX(), executingRobot.getTileY());
        if(cell == null? true:cell.getTileID() == -1? true : cell.getTileID() != MapTile.MOON_TURF) {
            executor.throwExecutionError(this, "This tile is not ploughable");
        } else {
            layer.setTileAt(executingRobot.getTileX(), executingRobot.getTileY(), MapTile.SOIL);
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
    
}
