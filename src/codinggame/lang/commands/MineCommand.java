/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.commands;

import codinggame.CodingGame;
import codinggame.handlers.CommandHandler;
import codinggame.handlers.MapHandler;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.lang.Parser;
import codinggame.lang.commands.utils.Direction;
import codinggame.objs.Inventory;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class MineCommand extends Command{

    private static final String FIRST_TOKEN = "mine";
    
    private Direction direction;
    
    public MineCommand(GameState game, CommandBlock parentCommandBlock, Robot executingRobot, CommandHandler executor, Direction direction) {
        super(game, parentCommandBlock, executingRobot, executor);
        this.direction = direction;
        if(executingRobot instanceof MinerRobot) {
            float miningSpeed = ((MinerRobot) executingRobot).getDrill().getItemType().getMiningSpeed();
            super.setMaxTime(2f / miningSpeed);
        }
    }

    @Override
    public void end() {
        super.end();
        int x = direction.getX(executingRobot.getTileX());
        int y = direction.getY(executingRobot.getTileY());
        CodingGame.debug(x + " " + y);
        MinerRobot miner = (MinerRobot) executingRobot;
        MapHandler mapHandler = game.getMapHandler();
        MapHandler.Break breakTile = mapHandler.breakTileEntity(miner.getInventory().getEquipment(EquipmentSlot.DRILL), x, y);
        if(breakTile == null) {
            executor.throwExecutionError(this, "Cannot mine tile");
            return;
        }
        Inventory inv = miner.getInventory();
        if(inv.addAll(breakTile.getItems())) {
            breakTile.destroy();
        } else {
            executor.throwExecutionError(this, "Not Enough Storage");
        }
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
//        MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).getTileAt(x, y);
//        cell.increaseBreakingProgress(delta / maxTime);
        
    }

    @Override
    public void begin() {
        super.begin();
        if(!(executingRobot instanceof MinerRobot)) {
            executor.throwExecutionError(this, "This robot is not a miner robot");
        }
    }
    
    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        Direction direction = Direction.get(parser, tokens[1]);
        if(!(executingRobot instanceof MinerRobot)) {
            parser.throwParsingError("This robot is not a miner robot");
        }
        return new MineCommand(game, parentCommandBlock, executingRobot, executor, direction);
    }
}
