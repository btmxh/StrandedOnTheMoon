/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.ItemType;
import com.lwjglwrapper.opengl.objects.Texture2D;

/**
 *
 * @author Welcome
 */
public class Drill extends CountItem{

    public Drill(Type itemType) {
        super(itemType, 1);
    }

    @Override
    public Drill clone() {
        return new Drill(getItemType());
    }

    @Override
    public Type getItemType() {
        return (Type) super.getItemType();
    }
    
    public static class Type extends Equipment {
    
        private float miningSpeed;

        public Type(Texture2D texture, String name, float miningSpeed) {
            super(texture, name, 0d);
            this.miningSpeed = miningSpeed;
        }

        public float getMiningSpeed() {
            return miningSpeed;
        }
    
    }
    
}
