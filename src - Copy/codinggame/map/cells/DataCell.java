/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells;

import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMapCell;

/**
 *
 * @author Welcome
 */
public abstract class DataCell extends ProcMapCell{
    
    public DataCell(MapTile tile) {
        super(tile);
    }
    
    public abstract Object toObject();
    public abstract void parseObject(Object container);
    
}
