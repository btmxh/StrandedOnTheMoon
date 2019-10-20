/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs;

import com.lwjglwrapper.utils.math.MathUtils;
import java.io.Serializable;

/**
 *
 * @author Welcome
 */
public class Battery implements Serializable{
    private double energyUnit;
    private double maxCapacity;

    public Battery(double energyUnit, double maxCapacity) {
        this.energyUnit = energyUnit;
        this.maxCapacity = maxCapacity;
    }

    public double getEnergy() {
        return energyUnit;
    }

    public double getMaxEnergyCapacity() {
        return maxCapacity;
    }

    public void increase(double amt) {
        energyUnit += amt;
        energyUnit = MathUtils.clamp(0.0, energyUnit, maxCapacity);
    }
    
    
    
    
}
