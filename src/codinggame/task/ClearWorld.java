/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.task;

import java.io.File;

/**
 *
 * @author Welcome
 */
public class ClearWorld {
    
    
    public static void main(String[] args) {
        File[] files = new File("saves/procmap/").listFiles();
        for (File file : files) {
            if(file.getName().endsWith("cnk"))  file.delete();
            else if(file.getName().endsWith("cnkd"))  file.delete();
        }
    }
    
}
