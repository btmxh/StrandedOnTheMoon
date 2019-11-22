/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.objs.Inventory;
import codinggame.utils.ByteArray;
import static codinggame.utils.Utils.ifNull;
import java.util.Arrays;

/**
 *
 * @author Welcome
 */
public class CellUtils {
    
    public static final byte NORMAL_CELL = 0, SOIL_CELL = 1, FACING_CELL = 2, INVENTORY_CELL = 3;
    
    //Inventory Cell Fields Locations
    public static final int INVENTORY = Integer.BYTES + Byte.BYTES;
    
    //Soil Cell
    public static final float REQUIRED_HYDRATION = 0.7f, DEFAULT_HYDRATION = 0.3f, DEFAULT_PH = 7.0f;
    public static final int HYDRATION = Integer.BYTES + Byte.BYTES, PH = HYDRATION + Float.BYTES;
    
    public static ProcMapCell inventoryCell(Inventory inv, int tileID) {
        int inv_stride = inv == null? 0:inv.stride();
        int stride = Integer.BYTES + Byte.BYTES + inv_stride;
        byte[] arr = new byte[stride];
        ByteArray.putInt(arr, 0, tileID);
        ByteArray.put(arr, ProcMapCell.TILE_TYPE, INVENTORY_CELL);
        if(inv != null) inv.writeTo(arr);
        return new ProcMapCell(arr);
    }
    
    public static Inventory inv_getInventory(ProcMapCell cell) {
        return null;
    }
    
    public static ProcMapCell soilCell(Float hydration, Float pH) {
        int stride = Integer.BYTES + Byte.BYTES + Float.BYTES * 2;
        byte[] arr = new byte[stride];
        ByteArray.putInt(arr, 0, MapTile.SOIL);
        ByteArray.put(arr, ProcMapCell.TILE_TYPE, SOIL_CELL);
        System.out.println(Arrays.toString(arr));
        ByteArray.putFloat(arr, HYDRATION, ifNull(hydration, DEFAULT_HYDRATION));
        ByteArray.putFloat(arr, PH, ifNull(pH, DEFAULT_PH));  //a pH of 7.0 is neutral
        return new ProcMapCell(arr);
    }
    
    public static void soil_setPH(ProcMapCell cell, Float pH) {
        ByteArray.putFloat(cell.data, PH, ifNull(pH, DEFAULT_PH));
    }
    
    public static void soil_setHydration(ProcMapCell cell, Float hydration) {
        ByteArray.putFloat(cell.data, HYDRATION, ifNull(hydration, DEFAULT_HYDRATION));
    }
    
    public static float soil_getPH(ProcMapCell cell) {
        return ByteArray.getFloat(cell.data, PH);
    }
    
    public static float soil_getHydration(ProcMapCell cell) {
        return ByteArray.getFloat(cell.data, HYDRATION);
    }

    public static String name(byte tileType) {
        switch(tileType) {
            case INVENTORY_CELL:    return "Storage Cell";
            case NORMAL_CELL:       return "Map Cell";
            case SOIL_CELL:         return "Soil Cell";
            case FACING_CELL:       return "Facing Cell";
        }
        throw new IllegalArgumentException(tileType + " is not a tile type");
    }

    public static ProcMapCell clone(ProcMapCell cell) {
        return new ProcMapCell(Arrays.copyOf(cell.data, cell.data.length));
    }
}
