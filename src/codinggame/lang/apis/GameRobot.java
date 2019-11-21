/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.apis;

import codinggame.lang.commands.CraftCommand;
import codinggame.lang.commands.EquipCommand;
import codinggame.lang.commands.GiveCommand;
import codinggame.lang.commands.GoCommand;
import codinggame.lang.commands.HarvestCommand;
import codinggame.lang.commands.HoeCommand;
import codinggame.lang.commands.MineCommand;
import codinggame.lang.commands.PlantCommand;
import codinggame.lang.commands.utils.Direction;
import codinggame.objs.RobotInventory;
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
    
    private GameRobotInventory inv;
    public RobotRadar radar;

    public GameRobot(GameState game, Robot robot) {
        super(game, robot);
        inv = new GameRobotInventory(game, robot, robot.getInventory());
        radar = new RobotRadar(game, robot);
    }
    
    public void go(int move, int direction) {
        executor.execute(new GoCommand(game, null, robot, executor, move, Direction.values()[direction]));
        super.lock();
        super.testInterupt();
    }
    
    public void mine(int direction) {
        executor.execute(new MineCommand(game, null, robot, executor, Direction.values()[direction]));
        super.lock();
        super.testInterupt();
    }
    
    public void craft(String item) {
        executor.execute(new CraftCommand(game, null, robot, executor, Recipes.getRecipe(ItemTypes.getItemByName(item))));
        super.lock();
        super.testInterupt();
    }
    
    public void equip(EquipmentSlot slot, String item) {
        executor.execute(new EquipCommand(game, null, robot, executor, slot, (Equipment) ItemTypes.getItemByName(item)));
        super.lock();
        super.testInterupt();
    }
    
    public void hoe() {
        executor.execute(new HoeCommand(game, null, robot, executor));
        super.lock();
        super.testInterupt();
    }
    
    public void plant(String item) {
        executor.execute(new PlantCommand(game, null, robot, executor, ItemTypes.getItemByName(item)));
        super.lock();
        super.testInterupt();
    }
    
    public void give(Robot robot, String item, double amount) {
        ItemType itemType = ItemTypes.getItemByName(item);
        Item giveItem;
        if(itemType instanceof ItemType.Count) {
            giveItem = new CountItem((ItemType.Count) itemType, (int) amount);
        } else if(itemType instanceof ItemType.Mass) {
            giveItem = new MassItem((ItemType.Mass) itemType, amount);
        } else giveItem = null;
        executor.execute(new GiveCommand(game, null, this.robot, executor, robot, giveItem));
        super.lock();
        super.testInterupt();
    }
    
    public void harvest() {
        executor.execute(new HarvestCommand(game, null, robot, executor));
        super.lock();
        super.testInterupt();
    }
    
    public int getX() {
        return robot.getTileX();
    }
    
    public int getY() {
        return robot.getTileY();
    }
    
    public GameInventory<RobotInventory> getInventory() {
        return inv;
    }

    public RobotRadar getRadar() {
        return radar;
    }
    
}
