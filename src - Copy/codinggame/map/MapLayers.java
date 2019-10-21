/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Welcome
 */
public class MapLayers extends ArrayList<MapLayer>{

    public MapLayers() {
    }
    
    public MapLayers(Collection<? extends MapLayer> collection) {
        super(collection);
    }
}
