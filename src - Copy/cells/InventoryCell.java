/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells;

import codinggame.CodingGame;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.proceduralmap.ProcMapCell;
import codinggame.objs.Inventory;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Loop;

/**
 *
 * @author Welcome
 */
public class InventoryCell extends DataCell{

    private Inventory inv;
    private static final double STORAGE_CELL_MAX_MASS = 100;
    
    public InventoryCell(MapTile tile) {
        super(tile);
        if(tile.getID() == MapTile.STORAGE_CELL) {
            inv = new Inventory(STORAGE_CELL_MAX_MASS);
        } else {
            //throw new IllegalArgumentException(tile.getID() + " is not a storage cell");
            inv = new Inventory(10);
        }
    }

    public Inventory getInventory() {
        return inv;
    }
    
    @Override
    public InventoryCell clone() {
        InventoryCell cell = new InventoryCell(tile);
        cell.inv = inv.clone();
        return cell;
    }

    @Override
    public void updateSavedData(MapTilesets tilesets) {
        super.updateSavedData(tilesets);
        CodingGame codingGame = (CodingGame) LWJGL.currentLoop;
        inv.refresh(codingGame.gs);
    }
    
    
    
    
}
