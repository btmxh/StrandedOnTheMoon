/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.equipments.Drill.Type;

/**
 *
 * @author Welcome
 */
public class Drill extends CountItem<Type>{

    public Drill(Type itemType) {
        super(itemType, 1);
    }

    @Override
    public Drill clone() {
        return new Drill(getItemType());
    }
    
    public static class Type extends Equipment {
    
        private float miningSpeed;

        public Type(String path, String name, float miningSpeed) {
            super(path, name, 0d);
            this.miningSpeed = miningSpeed;
        }

        public float getMiningSpeed() {
            return miningSpeed;
        }
    
    }
    
}
