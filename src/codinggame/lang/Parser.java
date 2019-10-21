/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.CodingGame;
import codinggame.handlers.GameUIHandler;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import org.joml.Vector2f;


/**
 *
 * @author Welcome
 */
public class Parser {

    private GameState game;
    private int processingLineIndex;
    private boolean processingFailed = false;
    

    public Parser(GameState game) {
        this.game = game;
    }
    
    public CommandBlock parse(String[] commands) {
        processingFailed = false;
        
        Robot executingRobot = game.getRobotHandler().getCurrentRobot();
                
        CommandBlock masterBlock = new CommandBlock(game, null, executingRobot, game.getCommandHandler());
        CommandBlock currentBlock = masterBlock;
        for (processingLineIndex = 0; processingLineIndex < commands.length; processingLineIndex++) {
            String command = commands[processingLineIndex].trim();
            if(command.isEmpty())   continue;
            if(command.startsWith("}")) {
                currentBlock = currentBlock.parentCommandBlock;
                continue;
            }
            Command currentCommand = Command.parse(this, currentBlock, executingRobot, command, game.getCommandHandler());
            currentBlock.addCommand(currentCommand);
            if(command.endsWith("{")) {
                if(currentCommand instanceof CommandBlock) {
                    currentBlock = (CommandBlock) currentCommand;
                } else {
                    throwParsingError("\"" + command + "\" can't be used to create a block");
                }
            }
            if(processingFailed) {
                return null;
            }
        }
        if(masterBlock != currentBlock) {
            throwParsingError("{} not enclosing");
            return null;
        }
        //masterBlock.exec();
        return masterBlock;
    }
    
    public CommandBlock parse(String block) {
        //System.out.println(processingFailed);
        return parse(block.split("\n"));
    }
    
    public int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            throwParsingError(string + " is not a valid number");
        }
        return 0;
    }
    
    public double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            throwParsingError(string + " is not a valid number");
        }
        return 0;
    }
    
    public void throwParsingError(String description) {
        GameUIHandler uiHandler = CodingGame.getInstance().gs.getUIHandler();
        uiHandler.println("Error parsing line number " + (processingLineIndex + 1) + ": " + description);
        processingFailed = true;
    }

    GameState getGameState() {
        return game;
    }

    public void reset() {
    }
}
