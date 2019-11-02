/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.commands;

import codinggame.handlers.CommandHandler;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.lang.Parser;
import codinggame.lang.commands.utils.Direction;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import org.joml.Vector2f;
import org.joml.Vector2i;


/**
 *
 * @author Welcome
 */
public class GoCommand extends Command{
    
    private static final String FIRST_TOKEN = "go";

    private Vector2i move;
    
    private Vector2f destination;
    
    public GoCommand(GameState game, CommandBlock parentCommandBlock, Robot executingRobot, CommandHandler executor, int move, Direction direction) {
        super(game, parentCommandBlock, executingRobot, executor);
        this.move = direction.moveVector(move);
        setEnergyConsumption(move);
    }

    @Override
    public void begin() {
        super.begin();
        setMaxTime((float) move.length() / executingRobot.getSpeed());
        destination = new Vector2f(executingRobot.getPosition()).add(move.x, move.y);
        for (int i = 0; i <= move.length(); i++) {
            int x = (int) (executingRobot.getTileX() + i * Math.signum(move.x));
            int y = (int) (executingRobot.getTileY() + i * Math.signum(move.y));
            MapCell cell = game.getMapHandler().getMap().getMapLayer(GameMap.ORE_LAYER).getTileAt(x, y);
            if(cell == null)    continue;
            if(cell.getTileType() == null)  continue;
            MapTile tile = cell.getTileType();
            if(tile.isSolid()) {
                executor.throwExecutionError(this, "The path is obstructed");
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        executingRobot.move(new Vector2f(move).mul(delta / maxTime));
    }

    @Override
    public void end() {
        super.end();
        executingRobot.moveTo(destination);
    }

    @Override
    public void undoCommand() {
        //executingRobot.moveTo(destination.add(direction.direction.mul(-amount, new Vector2f())));
    }
    
    @Override
    public void reset() {
        super.reset();
        destination = new Vector2f();
    }
    
    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        int moveX = parser.parseInt(tokens[1]);
        int moveY = parser.parseInt(tokens[2]);
        Vector2i move = new Vector2i(moveX, moveY);
        return new GoCommand(game, parentCommandBlock, executingRobot, executor, moveX, Direction.UP);
    }
}
