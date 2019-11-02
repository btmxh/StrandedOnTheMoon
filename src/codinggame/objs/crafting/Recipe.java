/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.crafting;

import codinggame.objs.Inventory;
import codinggame.objs.items.Item;
import codinggame.objs.robots.CraftingRobot;
import codinggame.objs.robots.MinerRobot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class Recipe {
    private List<Item> requirements;
    private List<Item> returnItems;

    public Recipe() {
        requirements = new ArrayList<>();
        returnItems = new ArrayList<>();
    }

    public Recipe setRequirements(List<Item> requirements) {
        this.requirements = requirements;
        return this;
    }

    public Recipe setReturnItems(List<Item> returnItems) {
        this.returnItems = returnItems;
        return this;
    }

    public Recipe setRequirements(Item... requirements) {
        List<Item> list = new ArrayList<>(Arrays.asList(requirements));
        return setRequirements(list);
    }

    public Recipe setReturnItems(Item... returnItems) {
        List<Item> list = new ArrayList<>(Arrays.asList(returnItems));
        return setReturnItems(list);
    }
    
    public Recipe(List<Item> requirements, List<Item> returnItems) {
        this.requirements = requirements;
        this.returnItems = returnItems;
    }
    
    public boolean craft(CraftingRobot robot) {
        Inventory inv = robot.getInventory();
        System.out.println(requirements);
        if(inv.removeAll(requirements)) {
            inv.addAll(returnItems);
            return true;
        }
        return false;
    } 

    public List<Item> getRequirements() {
        return requirements;
    }

    public List<Item> getReturnItems() {
        return returnItems;
    }
    
    
    
    
}
