/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.equipments.WateringCan.Type;

/**
 *
 * @author Welcome
 */
public class WateringCan extends CountItem<Type>{

    public WateringCan(Type itemType) {
        super(itemType, 1);
    }

    @Override
    public CountItem clone() {
        return new WateringCan(getItemType());
    }
    
    public static class Type extends ItemType.Count{
        
        public Type(String path, String name, double massPerItem) {
            super(path, name, massPerItem);
        }
        
    }
    
}
