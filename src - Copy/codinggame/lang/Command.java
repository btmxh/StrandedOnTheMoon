/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

import codinggame.handlers.CommandHandler;
import codinggame.lang.block.RepeatBlock;
import codinggame.lang.commands.CraftCommand;
import codinggame.lang.commands.EquipCommand;
import codinggame.lang.commands.GiveCommand;
import codinggame.lang.commands.GoCommand;
import codinggame.lang.commands.MineCommand;
import codinggame.lang.commands.WaitCommand;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public abstract class Command {


    protected GameState game;
    protected CommandHandler executor;
    protected CommandBlock parentCommandBlock;
    protected Robot executingRobot;
    protected float timer = 0;
    protected float maxTime;
    protected int energyConsumption;

    public Command(GameState game, CommandBlock parentCommandBlock,
            Robot executingRobot, CommandHandler executor) {
        this(game, parentCommandBlock, executingRobot, 1, executor);
    }

    public Command(GameState game, CommandBlock parentCommandBlock,
            Robot executingRobot, int energyConsumption, 
            CommandHandler executor) {
        this.game = game;
        this.energyConsumption = energyConsumption;
        this.parentCommandBlock = parentCommandBlock;
        this.executingRobot = executingRobot;
    }

    public void update(float delta) {
        timer += delta;
    }

    public boolean isOver() {
        return timer > maxTime;
    }

    public void reset() {
        timer = 0;
    }

    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    public void setEnergyConsumption(int energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public void begin() {
        if (executingRobot.getBattery().getEnergy() < energyConsumption) {
            getRootCommandBlock(this).forceStop();
        }
    }

    public void end() {
        executingRobot.getBattery().increase(-energyConsumption);
        synchronized (executingRobot.getLock()) {
            executingRobot.getLock().notifyAll();
        }
    }

    private static Map<String, Class<? extends Command>> commandClasses;
    
    public static Command parse(Parser parser, CommandBlock parentCommandBlock, Robot executingRobot,
            String command, CommandHandler executor) {
        if(commandClasses == null)  initializeClasses();
        GameState game = parsergs;
        String[] commandTokens = command.split("\\s+");
        Class<? extends Command> clazz = commandClasses.get(commandTokens[0]);
        if(clazz == null) {
            parser.throwParsingError("Unknown command " + commandTokens[0]);
            return null;
        } else {
            try {
                return (Command) clazz.getMethod("parseCommand", GameState.class, Parser.class, CommandBlock.class, Robot.class, String[].class, CommandHandler.class)
                        .invoke(null, game, parser, parentCommandBlock, executingRobot, commandTokens, executor);
            } catch (Exception ex) {
                Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
    
    private static void initializeClasses() {
        commandClasses = new HashMap<>();
        commandClasses.put("go", GoCommand.class);
        commandClasses.put("repeat", RepeatBlock.class);
        commandClasses.put("mine", MineCommand.class);
        commandClasses.put("give", GiveCommand.class);
        commandClasses.put("craft", CraftCommand.class);
        commandClasses.put("equip", EquipCommand.class);
        commandClasses.put("wait", WaitCommand.class);
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    public static CommandBlock getRootCommandBlock(Command command) {
        if (command.parentCommandBlock == null) {
            return (CommandBlock) command;
        } else {
            return getRootCommandBlock(command.parentCommandBlock);
        }
    }

    public abstract void undoCommand();
    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        return null;
    }

}
