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
import codinggame.objs.Inventory;
import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.Utils;

/**
 *
 * @author Welcome
 */
public class GiveCommand extends Command {
    
    private static final float GIVE_RADIUS = 1.5f;
    private static final String FIRST_TOKEN = "give";
    
    private Robot giveRobot;
    private Item give;
    
    public GiveCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot, String[] tokens, CommandHandler executor) {
        super(game, parser, parentCommandBlock, executingRobot, tokens, executor);
        //give cosi 3 Copper Ore
        String destinationID = tokens[1];
        boolean amountInCommand = false;
        try {
            Double.parseDouble(tokens[2]);
            amountInCommand = true;
        } catch (NumberFormatException nfe) {}
        String itemName = Utils.join(" ", (amountInCommand? 3:2), -1, tokens).trim();
        giveRobot = game.getRobotHandler().getRobot(destinationID);
        if(giveRobot == null) {
            parser.throwParsingError(destinationID + " is not a robot name");
        }
        ItemType itemType = ItemTypes.getItemByName(itemName);
        if(itemType == null) {
            parser.throwParsingError("There is no item such as " + itemName);
        }
        
        if(itemType instanceof ItemType.Mass) {
            double mass = amountInCommand? Double.parseDouble(tokens[2]):1;
            give = new MassItem((ItemType.Mass) itemType, mass);
        } else if(itemType instanceof ItemType.Count) {
            int amount = amountInCommand? parser.parseInt(tokens[2]):1;
            give = new CountItem((ItemType.Count) itemType, amount);
        }
    }

    @Override
    public void end() {
        super.end();
        boolean testRemove = executingRobot.getInventory().testRemove(give);
        boolean testAdd = giveRobot.getInventory().testAdd(give);
        
        if(executingRobot.getPosition().distance(giveRobot.getPosition()) > GIVE_RADIUS) {
            executor.throwExecutionError(this, "The robots are too far from each other");
            return;
        }
        
        if(!testRemove) {
            executor.throwExecutionError(this, executingRobot + " doesn't have the required item");
        } else if(!testAdd) {
            executor.throwExecutionError(this, giveRobot + " doesn't have enough storage space for new item");
        } else {
            Item sourceItem = executingRobot.getInventory().getItems().get(give.getItemType()).clone();
            if(give instanceof MassItem) {
                ((MassItem) sourceItem).setMass(give.getMass());
            } else if(give instanceof CountItem) {
                ((CountItem) sourceItem).setAmount(((CountItem) give).getAmount());
            }
            executingRobot.getInventory().remove(give);
            giveRobot.getInventory().add(sourceItem.clone());
        }
    }
    
    

    @Override
    public void undoCommand() {
    }
}
