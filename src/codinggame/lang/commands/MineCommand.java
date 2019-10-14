/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.commands;

import codinggame.handlers.CommandHandler;
import codinggame.handlers.MapHandler;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.lang.Parser;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.objs.Inventory;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class MineCommand extends Command{

    private static final String FIRST_TOKEN = "mine";
    
    private Direction direction;
    
    public MineCommand(GameState game, Parser parser, CommandBlock parentCommandBlock, Robot executingRobot, String[] tokens, CommandHandler executor) {
        super(game, parser, parentCommandBlock, executingRobot, tokens, executor);
        direction = Direction.get(parser, tokens[1]);
        if(executingRobot instanceof MinerRobot) {
            float miningSpeed = ((MinerRobot) executingRobot).getDrill().getItemType().getMiningSpeed();
            super.setMaxTime(2f / miningSpeed);
        } else super.setMaxTime(0);
    }

    @Override
    public void end() {
        super.end();
        int x = direction.getX(executingRobot.getTileX());
        int y = direction.getY(executingRobot.getTileY());
        MinerRobot miner = (MinerRobot) executingRobot;
        MapHandler mapHandler = game.getMapHandler();
        List<Item> drop = mapHandler.breakTile(miner.getInventory().getEquipment(EquipmentSlot.DRILL), GameMap.ORE_LAYER, x, y);
        if(drop == null) {
            executor.throwExecutionError(this, "Cannot mine tile");
            return;
        }
        Inventory inv = miner.getInventory();
        drop.forEach(inv::add);
    }

    @Override
    public void undoCommand() {
        //The command wouldn't have done anything if this is called
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        int x = direction.getX(executingRobot.getTileX());
        int y = direction.getY(executingRobot.getTileY());
        MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).getTileAt(x, y);
        cell.increaseBreakingProgress(delta / maxTime);
        
    }

    @Override
    public void begin() {
        super.begin();
        if(!(executingRobot instanceof MinerRobot)) {
            executor.throwExecutionError(this, "This robot is not a miner robot");
            return;
        }
        MinerRobot miner = (MinerRobot) executingRobot;
        MapHandler mapHandler = game.getMapHandler();
        int x = direction.getX(executingRobot.getTileX());
        int y = direction.getY(executingRobot.getTileY());
        Item drill = miner.getInventory().getEquipment(EquipmentSlot.DRILL);
    }
    
    
    
    
    
    private enum Direction {
        UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);
        
        private int x, y;

        private Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        private int getX(int robotX) {
            return x + robotX;
        }
        
        private int getY(int robotY) {
            return y + robotY;
        }
        
        private static Direction get(Parser parser, String name) {
            Direction[] directions = values();
            for (Direction direction : directions) {
                if(direction.name().equalsIgnoreCase(name)) {
                    return direction;
                }
            }
            parser.throwParsingError(name + " is not a valid go direction");
            return null;
        }
    }
}
