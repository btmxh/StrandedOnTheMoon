/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Welcome
 */
public class Clock {
    private static final GregorianCalendar START_DATE = new GregorianCalendar(2069, Calendar.JULY, 20);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    public static final int MILLIS_PER_MINUTE = 1000;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    
    public static final int MILLIS_PER_DAY = MILLIS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY;
    public static final int MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
    
    private long savedTime;
    private long gameTime;

    public Clock() {
    }

    public void setSavedTime(long savedTime) {
        this.savedTime = savedTime;
    }
    
    public void start() {
        gameTime = savedTime;
    }
    
    public void update(float delta) {
        gameTime += delta * 1000;
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
        return padZeros(getHour(), 2) + ":" + padZeros(getMinute(), 2);
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
    }

    public void stop() {
    }

    public String getDisplayDay() {
        GregorianCalendar temp = (GregorianCalendar) START_DATE.clone();
        temp.add(Calendar.DATE, (int) getDay());
        return DATE_FORMAT.format(temp.getTime());
    }

    public boolean between(float start, float end) {
        if(end < start) return between(end, start);
        float hour = (float) getInDayTime() / MILLIS_PER_HOUR;
        return start <= hour && hour <= end;
    }
}
