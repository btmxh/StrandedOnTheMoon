/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

/**
 *
 * @author Welcome
 */
public class Timer {
    private static long timer;
    public static void start() {
        timer = System.nanoTime();
    }
    
    public static void stop() {
        System.out.println(System.nanoTime() - timer);
    }
}
