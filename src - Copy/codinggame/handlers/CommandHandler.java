/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.CodingGame;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.states.GameState;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class CommandHandler {
    
    private GameState game;
    private List<CommandBlock> blocks;
    private List<Command> commands;
    private boolean stop;

    public CommandHandler(GameState game) {
        this.game = game;
        blocks = new ArrayList<>();
        commands = new ArrayList<>();
    }
    
    public void update(float delta) {
        for (Iterator<CommandBlock> it = blocks.iterator(); it.hasNext();) {
            CommandBlock block = it.next();
            if(block.isOver()) {
                block.end();
                it.remove();
            }
            block.update(delta);
        }
        for (Iterator<Command> it = commands.iterator(); it.hasNext();) {
            Command command = it.next();
            if(command.isOver()) {
                command.end();
                it.remove();
            }
            command.update(delta);
        }
    }
    
    public void execute(CommandBlock block) {
        if(block == null)   return;
        blocks.add(block);
        block.begin();
    }
    
    public void execute(Command command) {
        if(command == null) return;
        commands.add(command);
        command.begin();
    }
    
    public void stop() {
        blocks.forEach(CommandBlock::stop);
    }

    boolean isRunning() {
        return !blocks.isEmpty();
    }
    
    public void throwExecutionError(Command command, String description) {
        GameUIHandler uiHandler = CodingGame.getInstance()gs.getUIHandler();
        uiHandler.println("Execution error: " + description);
        Command.getRootCommandBlock(command).stop();
    }
}
