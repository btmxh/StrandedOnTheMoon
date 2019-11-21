/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.states.GameState;
import codinggame.utils.ArrayMap;
import codinggame.utils.ByteArray;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author Welcome
 */
public class Inventory {
    protected ArrayMap<ItemType, Item> items;
    protected double maxCapacity;
    protected double currentMass;

    public Inventory(double maxMassCapacity) {
        this.items = new ArrayMap<>();
        this.maxCapacity = maxMassCapacity;
        this.currentMass = 0;
    }
    
    public boolean add(Item item) {
        if(item instanceof MassItem)    return add((MassItem) item);
        if(item instanceof CountItem)    return add((CountItem) item);
        else return false;
    }
    
    private boolean add(MassItem item) {
        ItemType type = item.getItemType();
        MassItem existedItem = (MassItem) items.get(type);
        if(existedItem == null) {
            existedItem = item.clone();
            existedItem.setMass(0);
            items.put(type, existedItem);
        }
        if(currentMass + item.getMass() <= maxCapacity) {
            existedItem.setMass(existedItem.getMass()+ item.getMass());
            currentMass += item.getMass();
            item.setMass(0);
            return true;
        } else {
            existedItem.setMass(maxCapacity);
            currentMass = maxCapacity;
            item.setMass(existedItem.getMass() + item.getMass()- maxCapacity);
            return false;
        }
    }
    
    private boolean add(CountItem item) {
        ItemType.Count type = (ItemType.Count) item.getItemType();
        CountItem existedItem = (CountItem) items.get(type);
        if(existedItem == null) {
            existedItem = item.clone();
            existedItem.setAmount(0);
            items.put(type, existedItem);
        }
        if(currentMass + item.getMass() <= maxCapacity) {
            existedItem.setAmount(existedItem.getAmount() + item.getAmount());
            currentMass += item.getMass();
            item.setAmount(0);
            return true;
        } else {
            int newAmount = (int) (maxCapacity / type.getMassPerItem());
            existedItem.setAmount(newAmount);
            currentMass = maxCapacity;
            item.setAmount(existedItem.getAmount() + item.getAmount() - newAmount);
            return false;
        }
    }
    
    public boolean remove(Item item) {
        if(item instanceof MassItem)    return remove((ItemType.Mass) item.getItemType(), item.getMass());
        if(item instanceof CountItem)    return remove((ItemType.Count) item.getItemType(), ((CountItem) item).getAmount());
        return false;
    }
    
    private boolean remove(ItemType.Mass item, double mass) {
        System.out.println(item + " " + mass);
        MassItem inventoryItem = (MassItem) items.get(item);
        System.out.println(inventoryItem);
        if(inventoryItem.getMass() > mass) {
            System.out.println(inventoryItem.getMass() - mass);
            inventoryItem.setMass(inventoryItem.getMass() - mass);
            currentMass -= mass;
            return true;
        } else if(inventoryItem.getMass() == mass) {
            items.removeKey(item);
            currentMass -= mass;
            return true;
        } else {
            return false;
        }
    } 
    
    private boolean remove(ItemType.Count item, int amount) {
        CountItem inventoryItem = (CountItem) items.get(item);
        if(inventoryItem.getAmount() > amount) {
            inventoryItem.setAmount(inventoryItem.getAmount() - amount);
            currentMass -= item.getMassPerItem() * amount;
            return true;
        } else if(inventoryItem.getAmount() == amount) {
            items.removeKey(item);
            currentMass -= item.getMassPerItem() * amount;
            return true;
        } else return false;
    } 
    
    public void println() {
        System.out.println("Current Mass:" + currentMass);
        System.out.println(items);
    }
    
    public ArrayMap<ItemType, Item> getItems() {
        return items;
    }
    
    private String toString(Item i) {
        if(i instanceof MassItem)   return String.valueOf(((MassItem) i).getMass());
        else if(i instanceof CountItem) return String.valueOf(((CountItem) i).getAmount());
        else return "";
    }

    public double getCurrentCapacity() {
        return currentMass;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public boolean removeAll(List<Item> items) {
        if(!items.stream().allMatch((i) -> testRemove(i))) {
            return false;
        } else {
            items.forEach((i) -> this.remove((Item) i));
            return true;
        }
    }
    
    public boolean addAll(List<Item> items) {
        if(items.stream().mapToDouble(Item::getMass).sum() + currentMass > maxCapacity)
            return false;
        items.forEach((i) -> this.add((Item) i));
        return true;
    }
    
    public boolean testRemove(Item item) {
        Item existing = items.get(item.getItemType());
        if(existing == null)    return false;
        return existing.getMass() >= item.getMass();
    }
    
    public boolean testAdd(Item item) {
        return item.getMass() + currentMass <= maxCapacity;
    }
    
    public void refresh(GameState game) {
        //When the inventory is saved, the item is also saved, but it is different from the one that the game will load next time
        //So this method will replace all of the old items by the new ones
        ArrayMap<ItemType, Item> newMap = new ArrayMap<>();
        for (Pair<ItemType, Item> entry : items) {
            ItemType newType = ItemTypes.getItemByName(entry.getKey().getName());
            newMap.put(newType, entry.getValue().itemType(newType));
        }
        items.clear();
        items = newMap;
        currentMass = items.values().stream().mapToDouble(Item::getMass).sum();
    }
    
    @Override
    public Inventory clone() {
        Inventory clone = new Inventory(maxCapacity);
        clone.currentMass = currentMass;
        for (Pair<ItemType, Item> entry : items) {
            ItemType key = entry.getKey();
            Item value = entry.getValue();
            clone.items.put(key, value.clone());
        }
        return clone;
    }

    public int stride() {
        return 0;
    }

    public void writeTo(byte[] arr) {
    }
}
