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
import codinggame.objs.crafting.Recipe;
import codinggame.objs.crafting.Recipes;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.robots.CraftingRobot;
import codinggame.objs.robots.MinerRobot;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.utils.Utils;
import java.util.UUID;

/**
 *
 * @author Welcome
 */
public class CraftCommand extends Command{
    
    private static final String FIRST_TOKEN = "craft";

    private Recipe recipe;

    public CraftCommand(GameState game,
            CommandBlock parentCommandBlock, Robot executingRobot,
            CommandHandler executor, Recipe recipe) {
        super(game, parentCommandBlock, executingRobot, executor);
        this.recipe = recipe;
        //TODO: throw error
        setMaxTime(1.5f);
    }

    @Override
    public void end() {
        super.end();
        if(executingRobot instanceof CraftingRobot) {
            System.out.println(executingRobot.getInventory().getItems());
            boolean craft = recipe.craft((CraftingRobot) executingRobot);
        } else {
        }
    }

    
    
    @Override
    public void undoCommand() {
        
    }

    public static Command parseCommand(GameState game, Parser parser,
            CommandBlock parentCommandBlock, Robot executingRobot,
            String[] tokens, CommandHandler executor) {
        String craftItem = Utils.join(" ", 1, -1, tokens);
        ItemType type = ItemTypes.getItemByName(craftItem);
        if(type == null)    parser.throwParsingError("There is no item such as " + type);
        Recipe recipe = Recipes.getRecipe(type);
        if(recipe == null)  parser.throwParsingError("There is no recipe to craft " + type);
        return new CraftCommand(game, parentCommandBlock, executingRobot, executor, recipe);
    }
    
}
