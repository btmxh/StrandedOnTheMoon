/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import com.lwjglwrapper.display.Keyboard;

/**
 * 
 * @author Welcome
 */
public class KeyTimer {
    private int key;
    private long firstTimer, repeatTimer;

    public KeyTimer(int key, long firstTimer, long repeatTimer) {
        this.key = key;
        this.firstTimer = firstTimer;
        this.repeatTimer = repeatTimer;
    }

    /**
     * @param key the key to set
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * @param firstTimer the firstTimer to set
     */
    public void setFirstTimer(long firstTimer) {
        this.firstTimer = firstTimer;
    }

    /**
     * @param repeatTimer the repeatTimer to set
     */
    public void setRepeatTimer(long repeatTimer) {
        this.repeatTimer = repeatTimer;
    }
    
    private long lastTrue;
    private long timer;
    
    public boolean test(Keyboard keyboard) {
        if(keyboard.keyPressed(key)) {
            lastTrue = System.currentTimeMillis();
            timer = firstTimer;
            return true;
        } else if(keyboard.keyReleased(key)) {
            lastTrue = -1;
            timer = firstTimer;
            return false;
        } else if(keyboard.keyDown(key)) {
            long ellapsed = System.currentTimeMillis() - lastTrue - timer;
            if(ellapsed > 0) {
                lastTrue = System.currentTimeMillis();
                timer = repeatTimer;
                return true;
            }
            return false;
        } else return false;
    }
}
