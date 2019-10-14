/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs;

/**
 *
 * @author Welcome
 */
public class Clock {
    private static final int MILLIS_PER_MINUTE = 1000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    
    private static final int MILLIS_PER_DAY = MILLIS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY;
    private static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
    
    private long savedTime;
    private long startTime;
    private long gameTime;

    public Clock() {
        this.start();
    }

    public void setSavedTime(long savedTime) {
        this.savedTime = savedTime;
    }
    
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void update() {
        gameTime = System.currentTimeMillis() - startTime + savedTime;
    }

    public long getGameTime() {
        return gameTime;
    }
    
    public long getDay() {
        return gameTime / MILLIS_PER_DAY;
    }
    
    public int getInDayTime() {
        return (int) (gameTime % MILLIS_PER_DAY);
    }
    
    public int getHour() {
        return getInDayTime() / MILLIS_PER_HOUR;
    }
    
    public int getMinute() {
        return (getInDayTime() % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
    }

    public String getDisplayTime() {
        return "(Day " + getDay() + ") " + padZeros(getHour(), 2) + ":" + padZeros(getMinute(), 2);
    }
    
    public boolean isDay() {
        int hour = getHour();
        return hour >= 5 && hour < 19;
    }
    
    public boolean isNight() {
        return !isDay();
    }
    
    private static String padZeros(int number, int length) {
        String string = String.valueOf(number);
        while (string.length() < length) {
            string = "0" + string;
        }
        return string;
    }
    
    public static void main(String[] args) {
        Clock clock = new Clock();
        clock.setSavedTime(Long.MAX_VALUE);
        clock.update();
        System.out.println(clock.getDisplayTime());
    }
}
