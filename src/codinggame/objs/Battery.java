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
    private int energyUnit;
    private int maxCapacity;

    public Battery(int energyUnit, int maxCapacity) {
        this.energyUnit = energyUnit;
        this.maxCapacity = maxCapacity;
    }

    public int getEnergy() {
        return energyUnit;
    }

    public int getMaxEnergyCapacity() {
        return maxCapacity;
    }

    public void increase(int amt) {
        energyUnit += amt;
        energyUnit = MathUtils.clamp(0, energyUnit, maxCapacity);
    }
    
    
    
    
}
