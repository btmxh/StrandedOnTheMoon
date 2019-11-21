/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.objs.Inventory;
import codinggame.utils.ByteArray;

/**
 *
 * @author Welcome
 */
public class CellBuilder {
    
    public static final byte NORMAL_CELL = 0, SOIL_CELL = 1, FACING_CELL = 2, INVENTORY_CELL = 3;
    
    //Inventory Cell Fields Locations
    public static final int INVENTORY = Integer.BYTES + Byte.BYTES;
    
    //Soil Cell Fields Locations
    
    public static ProcMapCell inventoryCell(Inventory inv, int tileID) {
        int inv_stride = inv == null? 0:inv.stride();
        int stride = Integer.BYTES + Byte.BYTES + inv_stride;
        byte[] arr = new byte[stride];
        ByteArray.putInt(arr, 0, tileID);
        ByteArray.put(arr, ProcMapCell.TILE_ID, INVENTORY_CELL);
        if(inv != null) inv.writeTo(arr);
        return new ProcMapCell(arr, stride);
    }
    
    public static Inventory getInventoryFromCell(ProcMapCell cell) {
        return null;
    }
    
    
}
