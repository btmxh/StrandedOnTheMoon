/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells;

import codinggame.map.MapTile;
import codinggame.map.MapTileSheet;
import org.joml.Vector4f;

/**
 *
 * @author Welcome
 */
public class SheetCell extends DataCell{
    
    private int index;
    
    public SheetCell(MapTileSheet tile, int index) {
        super(tile);
        this.index = index;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public SheetCell clone() {
        return new SheetCell((MapTileSheet) tile, index);
    }
    
    public Vector4f calculateBounds() {
        return calculateBounds(false);
    }

    @Override
    public MapTileSheet getTileType() {
        return (MapTileSheet) super.getTileType(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    public Vector4f calculateBounds(boolean flipY) {
        MapTileSheet tileSheet = (MapTileSheet) tile;
        int texX = index % tileSheet.getNumberOfCols();
        int texY = index / tileSheet.getNumberOfCols();
        float x1 = (float) texX / tileSheet.getNumberOfCols();
        float y1 = (float) texY / tileSheet.getNumberOfRows();
        Vector4f bounds = new Vector4f(x1, y1, x1 + 1f/ tileSheet.getNumberOfCols(), 1f / tileSheet.getNumberOfRows());
        if(flipY)   bounds.y = 1 - bounds.y;
        return bounds;
    }
    
}
