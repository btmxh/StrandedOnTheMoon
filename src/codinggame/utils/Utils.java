/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import codinggame.objs.robots.CraftingRobot;
import codinggame.objs.robots.Robot;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 * @author Welcome
 */
public class Utils {
    public static String toVariableNameString(String string) {
        //Miner Robot -> MINER_ROBOT
        return String.join("_", Stream.of(string.split("\\s+")).map(String::toUpperCase).toArray(String[]::new));
    }
    
    public static String toReadableString(String string) {
        //MINER_ROBOT -> Miner Robot
        String[] words = string.split("_");
        for (int i = 0; i < words.length; i++) {
            if(words[i].length() <= 1)  continue;
            words[i] = words[i].toLowerCase();
            words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        }
        return String.join(" ", words);
    }
    
    public static String join(String delimiter, int start, int end, String... strings) {
        if(start < 0)   return join(delimiter, 0, end, strings);
        else if(end < 0)    return join(delimiter, start, strings.length - 1, strings);
        
        String[] ranged = Arrays.copyOfRange(strings, start, end + 1);
        return String.join(delimiter, ranged);
    }

    public static void main(String[] args) {
        Robot r = new CraftingRobot(null, null, "ds");
        System.out.println(r.getTypeName());
    }
}
