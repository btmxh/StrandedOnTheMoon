/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.handlers.CommandHandler;
import codinggame.lang.CommandBlock;
import codinggame.lang.commands.CraftCommand;
import codinggame.lang.commands.EquipCommand;
import codinggame.lang.commands.GiveCommand;
import codinggame.lang.commands.GoCommand;
import codinggame.lang.commands.MineCommand;
import codinggame.objs.crafting.Recipes;
import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.items.equipments.Equipment;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import org.joml.Vector2i;

/**
 *
 * @author Welcome
 */
public class GameRobot extends GameObject{

    public GameRobot(GameState game, Robot robot) {
        super(game, robot);
    }
    
    public void go(int moveX, int moveY) {
        executor.execute(new GoCommand(game, null, robot, executor, new Vector2i(moveX, moveY)));
        super.lock();
    }
    
    public void mine(int direction) {
        executor.execute(new MineCommand(game, null, robot, executor, MineCommand.Direction.values()[direction]));
        super.lock();
    }
    
    public void craft(String item) {
        executor.execute(new CraftCommand(game, null, robot, executor, Recipes.getRecipe(ItemTypes.getItemByName(item))));
        super.lock();
    }
    
    public void equip(EquipmentSlot slot, String item) {
        executor.execute(new EquipCommand(game, null, robot, executor, slot, (Equipment) ItemTypes.getItemByName(item)));
        super.lock();
    }
    
    public void give(Robot robot, String item, double amount) {
        ItemType itemType = ItemTypes.getItemByName(item);
        Item giveItem;
        if(itemType instanceof ItemType.Count) {
            giveItem = new CountItem((ItemType.Count) itemType, (int) amount);
        } else if(itemType instanceof ItemType.Mass) {
            giveItem = new MassItem((ItemType.Mass) itemType, amount);
        } else giveItem = null;
        executor.execute(new GiveCommand(game, currentBlock, robot, executor, robot, giveItem));
        super.lock();
    }
    
}
