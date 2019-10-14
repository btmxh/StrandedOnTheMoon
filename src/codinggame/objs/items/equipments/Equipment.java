/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items.equipments;

import codinggame.objs.items.ItemType;
import com.lwjglwrapper.opengl.objects.Texture2D;

/**
 *
 * @author Welcome
 */
public class Equipment extends ItemType.Count{
    
    public Equipment(Texture2D texture, String name, double massPerItem) {
        super(texture, name, massPerItem);
    }
    
}
