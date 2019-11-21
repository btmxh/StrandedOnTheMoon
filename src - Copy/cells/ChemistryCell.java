/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells;

import codinggame.chemistry.ElementGroup;
import codinggame.map.MapTile;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class ChemistryCell extends DataCell{
    
    private Map<ElementGroup, Float> masses;

    public ChemistryCell(MapTile tile) {
        super(tile);
        masses = new HashMap<>();
    }

    
}
