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
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import org.joml.Vector2f;


/**
 *
 * @author Welcome
 */
public class GoCommand extends Command{
    
    private static final String FIRST_TOKEN = "go";

    private Direction direction;
    private int amount;
    
    private Vector2f destination;
    
    public GoCommand(GameState game, Parser parser, CommandBlock parentCommandBlock, Robot executingRobot, String[] tokens, CommandHandler executor) {
        super(game, parser, parentCommandBlock, executingRobot, tokens, executor);
        this.direction = Direction.get(parser, tokens[1]);
        this.amount = tokens.length == 2? 1:parser.parseInt(tokens[2]);
        setMaxTime(amount * 1f);
        setEnergyConsumption(amount * 1);
    }

    @Override
    public void begin() {
        super.begin();
        destination = new Vector2f(executingRobot.getPosition()).add(direction.direction.mul(amount, new Vector2f()));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        executingRobot.move(direction.direction.mul(amount * delta / maxTime, new Vector2f()));
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
    
    
    
    

    private enum Direction {
        UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);
        
        private Vector2f direction;

        private Direction(int x, int y) {
            this.direction = new Vector2f(x, y);
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
