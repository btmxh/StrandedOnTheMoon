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

/**
 *
 * @author Welcome
 */
public class WaitCommand extends Command{

    private static final String FIRST_TOKEN = "wait";
    
    public WaitCommand(GameState game,
            CommandBlock parentCommandBlock, Robot executingRobot,
            CommandHandler executor, float time) {
        super(game, parentCommandBlock, executingRobot, executor);
        super.setMaxTime(time);
    }

    @Override
    public void undoCommand() {
    }
    
    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        return new WaitCommand(game, parentCommandBlock, executingRobot, executor, (float) parser.parseDouble(tokens[1]));
    }

}
