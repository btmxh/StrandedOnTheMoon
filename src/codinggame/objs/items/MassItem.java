/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import java.util.Locale;

/**
 *
 * @author Welcome
 */
public class MassItem extends Item{
    private double mass;

    public MassItem(ItemType.Mass itemType, double mass) {
        super(itemType);
        this.mass = mass;
    }
    
    @Override
    public ItemType.Mass getItemType() {
        return (ItemType.Mass) super.getItemType();
    }

    @Override
    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
    
    @Override
    public String toString() {
        return type.getName() + " (" + String.format(Locale.US, "%.1f", mass) + " kg)";
    }

    @Override
    public Item multiply(double times) {
        return new MassItem(getItemType(), mass * times);
    }

    @Override
    public MassItem clone() {
        return new MassItem(getItemType(), mass);
    }
    
    
}
