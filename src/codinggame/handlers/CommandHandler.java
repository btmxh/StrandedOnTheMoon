/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.CodingGame;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.lang.StopException;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.ui.codingarea.CodingFX;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class CommandHandler {
    
    private GameState game;
    private List<CommandBlock> blocks;
    private final List<Command> commands;
    private boolean stop;
    
    private Map<Robot, Thread> threads;

    public CommandHandler(GameState game) {
        this.game = game;
        blocks = new ArrayList<>();
        commands = new ArrayList<>();
        threads = new HashMap<>();
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
        synchronized (commands) {
//            for (Iterator<Command> it = commands.iterator(); it.hasNext();) {
//                Command command = it.next();
//                if(command.isOver()) {
//                    command.end();
//                    it.remove();
//                }
//                command.update(delta);
//            }
            for (int i = commands.size() - 1; i >= 0; i--) {
                Command command = commands.get(i);
                if(command.isOver()) {
                    command.end();
                    commands.remove(i);
                }
                command.update(delta);
            }
        }
    }
    
    public void execute(CommandBlock block) {
        if(block == null)   return;
        blocks.add(block);
        block.begin();
    }
    
    public void execute(Command command) {
        if(command == null) return;
        command.begin();
        synchronized (commands) {
            commands.add(command);
        }
    }
    
    public void addThread(Robot robot, Thread thread) {
        threads.put(robot, thread);
        thread.start();
    }
    
    public void stop() {
        blocks.forEach(CommandBlock::stop);
    }

    boolean isRunning() {
        return !blocks.isEmpty();
    }
    
    public void throwExecutionError(Command command, String description) {
        try {
            CodingFX.currentController.println("Execution error: " + description);
            command.getRobot().stopCommands();
            Command.getRoot(command).forceStop();
        } catch (StopException ex) {
            
        }
    }

    public void stop(Robot currentRobot) {
        currentRobot.stopCommands();
    }
}
