/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs;

import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.modules.Module;
import codinggame.states.GameState;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javafx.util.Pair;

/**
 *
 * @author Welcome
 */
public class RobotInventory extends Inventory implements Serializable{
    
    public static final int MODULES = 5;
    
    private Map<EquipmentSlot, Item> equipments;
    private Module[] modules;
    
    public RobotInventory(double maxMassCapacity) {
        super(maxMassCapacity);
        equipments = new HashMap<>();
        modules = new Module[MODULES];
    }
    
    public void equip(EquipmentSlot slot, Item item) {
        equipments.put(slot, item);
        currentMass += item.getMass();
    }
    
    public void unequip(EquipmentSlot slot) {
        Item item = equipments.remove(slot);
        currentMass -= item.getMass();
    }

    public Item getEquipment(EquipmentSlot slot) {
        return equipments.get(slot);
    }

    public boolean equipInInventory(EquipmentSlot slot, ItemType.Count type) {
        //Take an item from inventory and equip
        if(getEquipment(slot) != null) {
            unequipToInventory(slot);
        } 
        Item item = items.get(type);
        if(item == null)    return false;
        if(remove(item)) {
            System.out.println(items);
            equip(slot, item);
            return true;
        }
        return false;
    }
    
    public void unequipToInventory(EquipmentSlot slot) {
        //Similar to equipInInventory
        Item equipment = getEquipment(slot);
        unequip(slot);
        add(equipment);
    }
    
    public void setModule(int slot, Module module) {
        if(slot < 0 || slot >= MODULES) {
            return;
        }
        modules[slot] = module;
    }

    @Override
    public void refresh(GameState game) {
        super.refresh(game);
        for (EquipmentSlot slot : equipments.keySet()) {
            Item item = equipments.get(slot);
            Item newItem = item.itemType(ItemTypes.getItemByName(item.getItemType().getName()));
            equipments.replace(slot, newItem);
        }
        for (int i = 0; i < MODULES; i++) {
            if(modules[i] == null)  continue;
            modules[i].setGameState(game);
        }
    }

    public Map<EquipmentSlot, Item> getEquipments() {
        return equipments;
    }

    public Module[] getModules() {
        return modules;
    }

    @Override
    public RobotInventory clone() {
        RobotInventory inv = new RobotInventory(maxCapacity);
        inv.currentMass = currentMass;
        for (Pair<ItemType, Item> entry : items) {
            ItemType key = entry.getKey();
            Item value = entry.getValue();
            inv.items.put(key, value.clone());
        }
        for (Map.Entry<EquipmentSlot, Item> entry : equipments.entrySet()) {
            EquipmentSlot key = entry.getKey();
            Item value = entry.getValue();
            inv.equipments.put(key, value.clone());
        }
        inv.modules = Stream.of(modules).map(Module::clone).toArray(Module[]::new);
        return inv;
    }
    
    
    
}
