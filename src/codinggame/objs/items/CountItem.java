/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

/**
 *
 * @author Welcome
 */
public class CountItem<T extends ItemType.Count> extends Item<T> {
    private int amount;

    public CountItem(T itemType, int amount) {
        super(itemType);
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public double getMass() {
        return getItemType().getMassPerItem() * amount;
    }

    @Override
    public String toString() {
        if(amount == 1) return type.getName();
        return type.getName() + " x " + amount;
    }

    @Override
    public Item multiply(double times) {
        return new CountItem(getItemType(), (int) (amount * times));
    }

    @Override
    public CountItem clone() {
        return new CountItem(getItemType(), amount);
    }
    
    
    
    
}
