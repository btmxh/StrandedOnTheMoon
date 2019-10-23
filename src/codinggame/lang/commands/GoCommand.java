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
import org.joml.Vector2i;


/**
 *
 * @author Welcome
 */
public class GoCommand extends Command{
    
    private static final String FIRST_TOKEN = "go";

    private Vector2i move;
    
    private Vector2f destination;
    
    public GoCommand(GameState game, CommandBlock parentCommandBlock, Robot executingRobot, CommandHandler executor, Vector2i move) {
        super(game, parentCommandBlock, executingRobot, executor);
        this.move = move;
        setEnergyConsumption((int) (move.length() * 1));
    }

    @Override
    public void begin() {
        super.begin();
        setMaxTime((float) move.length() / executingRobot.getSpeed());
        destination = new Vector2f(executingRobot.getPosition()).add(move.x, move.y);
        System.out.println(destination);
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
        return new GoCommand(game, parentCommandBlock, executingRobot, executor, move);
    }
}
