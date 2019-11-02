/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

/**
 *
 * @author Welcome
 */
abstract class RobotThread extends Thread implements Runnable{

    private boolean running = true;
    
    public RobotThread() {
    }

    @Override
    public abstract void run();

    @Override
    public synchronized void start() {
        super.start();
        running = true;
    }
    
    
    
}
