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
    
    public WaitCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        super(game, parser, parentCommandBlock, executingRobot, tokens, executor);
        super.setMaxTime((float) parser.parseDouble(tokens[1]));
    }

    @Override
    public void undoCommand() {
    }
    
}
