/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.objs.Clock;
import com.lwjglwrapper.LWJGL;

/**
 *
 * @author Welcome
 */
public class SurvivalHandler {
    
    public static final float OXYGEN_CONSUME_PER_GAME_SECOND = 0.0001f;
    public static final float OXYGEN_CONSUME_PER_SECOND = OXYGEN_CONSUME_PER_GAME_SECOND * 60;  //an in-game minute is a second irl
    
    public static final float FOOD_CONSUME_PER_MEAL_SECOND = 0.01f;
    public static final float FOOD_CONSUME_PER_SECOND = FOOD_CONSUME_PER_MEAL_SECOND * 60;
    
    public static final float WATER_CONSUME_PER_GAME_SECOND = 0.01f;
    public static final float WATER_CONSUME_PER_SECOND = WATER_CONSUME_PER_GAME_SECOND * 60;
    
    private float last_oxygen;
    private float oxygen;
    
    private float last_food;
    private float food;
    
    private float last_water;
    private float water;

    public SurvivalHandler() {
    }

    public SurvivalHandler(float oxygen, float food, float water) {
        this.last_oxygen = oxygen;
        this.oxygen = oxygen;
        this.last_food = food;
        this.food = food;
        this.last_water = water;
        this.water = water;
    }
    
    
    
    public float oxygenRate() { //per in-game hour
        return (float) ((oxygen - last_oxygen) / LWJGL.currentLoop.getDeltaTime() * 60f);
    }
    
    public float foodRate() { //per in-game hour
        return (float) ((food - last_food) / LWJGL.currentLoop.getDeltaTime() * 60f);
    }
    
    public float waterRate() { //per in-game hour
        return (float) ((water - last_water) / LWJGL.currentLoop.getDeltaTime() * 60f);
    }
    
    public void update(Clock clock, float delta) {
        last_oxygen = oxygen;
        oxygen -= OXYGEN_CONSUME_PER_SECOND * delta;
        
        last_food = food;
        if(clock.between(5f, 7f) || clock.between(11.5f, 12f) || clock.between(18f, 18.5f)) {   //Meal times
            food -= FOOD_CONSUME_PER_SECOND * delta;
        }
        
        last_water = water;
        if(Math.random() * 60 < delta) {    //avg one hour drink
            water -= WATER_CONSUME_PER_SECOND * delta;
        }
    }

    /**
     * @return the oxygen
     */
    public float getOxygen() {
        return oxygen;
    }

    /**
     * @return the food
     */
    public float getFood() {
        return food;
    }

    /**
     * @return the water
     */
    public float getWater() {
        return water;
    }

    public float getFoodPercentage() {
        return food / 100f;
    }

    public float getWaterPercentage() {
        return water / 100f;
    }
    
    public float getOxygenPercentage() {
        return oxygen / 100f;
    }
    
}
