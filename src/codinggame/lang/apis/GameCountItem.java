/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.apis;

import codinggame.objs.items.CountItem;

/**
 *
 * @author Welcome
 */
public class GameCountItem extends GameItem<CountItem>{

    GameCountItem(CountItem countItem) {
        super(countItem);
    }

    public int getCount() {
        return item.getAmount();
    }
    
}
