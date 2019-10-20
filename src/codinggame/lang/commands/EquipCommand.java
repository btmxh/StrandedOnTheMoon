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
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.equipments.Equipment;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.Utils;

/**
 *
 * @author Welcome
 */
public class EquipCommand extends Command{

    private static final String FIRST_TOKEN = "equip";
    
    private EquipmentSlot slot;
    private Equipment type;

    public EquipCommand(GameState game,
            CommandBlock parentCommandBlock, Robot executingRobot,
            CommandHandler executor, EquipmentSlot slot, Equipment type) {
        super(game, parentCommandBlock, executingRobot, executor);
        this.slot = slot;
        this.type = type;
        setMaxTime(0f);
    }

    @Override
    public void undoCommand() {
    }

    @Override
    public void end() {
        super.end();
        executingRobot.getInventory().equipInInventory(slot, type);
    }

    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        EquipmentSlot slot = EquipmentSlot.getFromName(tokens[1]);
        if(slot == null) {
            parser.throwParsingError("There is no slot such as " + slot);
        }
        String itemName = Utils.join(" ", 2, -1, tokens);
        ItemType type = ItemTypes.getItemByName(itemName);
        if(!(type instanceof Equipment)) {
            parser.throwParsingError(type + " is not a equipment");
        }
        return new EquipCommand(game, parentCommandBlock, executingRobot, executor, slot, (Equipment) type);
    }
    
    
    
}
