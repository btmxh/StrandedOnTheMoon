/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import codinggame.objs.items.CountItem;
import codinggame.objs.items.Item;

/**
 *
 * @author Welcome
 */
public class ModuleItem extends CountItem<Module>{
    
    public ModuleItem(Module itemType) {
        super(itemType, 1);
    }

    @Override
    public ModuleItem clone() {
        return new ModuleItem(type.clone());
    }
    
}
