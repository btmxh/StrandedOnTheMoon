/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.block;

import codinggame.handlers.CommandHandler;
import codinggame.lang.Command;
import codinggame.lang.CommandBlock;
import codinggame.lang.Parser;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;

/**
 *
 * @author Welcome
 */
public class RepeatBlock extends CommandBlock {

    private static final String FIRST_TOKEN = "repeat";
    
    private int executed = 0;
    private int times;

    public RepeatBlock(GameState game, Parser parser, CommandBlock parentCommandBlock, Robot executingRobot, String[] tokens,CommandHandler executor) {
        super(game, parser, parentCommandBlock, executingRobot, executor);
        times = parser.parseInt(tokens[1]);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(isOver()) {
            if(executed + 1 < times) {
                executing = 0;
                executed++;
                commands.forEach(Command::reset);
                begin();
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        executed = 0;
    }

    @Override
    public int getEnergyConsumption() {
        return super.getEnergyConsumption() * times;
    }
    
    
    
    

}
