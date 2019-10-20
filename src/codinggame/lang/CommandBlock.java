/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.handlers.CommandHandler;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class CommandBlock extends Command {

    protected List<Command> commands;
    protected int executing = 0;
    
    private boolean stop = false;
    private boolean pendStop = false;

    public CommandBlock(GameState game,
            CommandBlock parentCommandBlock, Robot executingRobot, CommandHandler executor) {
        super(game, parentCommandBlock, executingRobot, 0, executor);
        commands = new ArrayList<>();
    }

//    @Override
//    public void exec() {
//        commands.forEach(Command::exec);
//    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void reset() {
        commands.forEach(Command::reset);
        executing = 0;
        stop = false;
    }

    @Override
    public boolean isOver() {
        if(pendStop)    return true;
        if (commands.isEmpty()) {
            return true;
        }
        return commands.get(commands.size() - 1).isOver();
    }

    @Override
    public void update(float delta) {
        if (isOver() || pendStop) {
            return;
        }
        Command executingCommand = commands.get(executing);
        executingCommand.update(delta);
        if (executingCommand.isOver()) {
            pendStop = stop;
            executingCommand.end();
            executing++;
            if(!isOver()) {
                commands.get(executing).begin();
            }
        }
    }

    @Override
    public void begin() {
        if(commands.isEmpty()) return;
        commands.get(0).begin();
        executingRobot.beginMoving();
    }

    @Override
    public void end() {
        executingRobot.endMoving();
    }

    public void stop() {
        stop = true;
        if(executing < commands.size()) {
            Command executingCommand = commands.get(executing);
            if(executingCommand instanceof CommandBlock) {
                ((CommandBlock) executingCommand).stop();
            }
        }
    }

    @Override
    public int getEnergyConsumption() {
        return energyConsumption + commands.stream().mapToInt(Command::getEnergyConsumption).sum();
    }

    public void forceStop() {
        pendStop = true;
        undoCommand();
    }

    @Override
    public void undoCommand() {
        commands.get(executing).undoCommand();
    }

    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        return new CommandBlock(game, parentCommandBlock, executingRobot, executor);
    }
}
