/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.crafting;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.items.equipments.Drill;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class Recipes {
    public static List<Recipe> recipes;
    
    static {
        recipes = new ArrayList<>();
        add(new Recipe().setRequirements(new MassItem(ItemTypes.COPPER_ORE, 10)).setReturnItems(new Drill(ItemTypes.COPPER_DRILL)));
        add(new Recipe().setRequirements(new CountItem(ItemTypes.POTATO, 2)).setReturnItems(new CountItem(ItemTypes.POTATO_CAN, 1)));
        add(new Recipe().setRequirements(new MassItem(ItemTypes.IRON_ORE, 5f)).setReturnItems(new CountItem(ItemTypes.BUCKET, 1)));
        add(new Recipe().setRequirements(new CountItem(ItemTypes.BUCKET, 1), new MassItem(ItemTypes.ICE, 3f)).setReturnItems(new CountItem(ItemTypes.WATER_BUCKET, 1)));
    }
    
    public static void add(Recipe recipe) {
        recipes.add(recipe);
    }

    public static Recipe getRecipe(ItemType item) {
        for (Recipe recipe : recipes) {
            for (Item returnItem : recipe.getReturnItems()) {
                if(returnItem.getItemType() == item)    return recipe;
            }
        }
        return null;
    }
}
