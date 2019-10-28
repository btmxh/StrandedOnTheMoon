/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.items.CountItem;

/**
 *
 * @author Welcome
 */
public class Hoe extends CountItem<Hoe.Type>{

    public Hoe(Type itemType) {
        super(itemType, 1);
    }
    
    public static class Type extends Equipment{
        
        private float speed;
        
        public Type(String path, String name, float speed) {
            super(path, name, 0d);
            this.speed = speed;
        }

        public float getSpeed() {
            return speed;
        }
        
        
        
    }
}
