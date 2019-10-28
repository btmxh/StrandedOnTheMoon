/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import com.lwjglwrapper.opengl.objects.Texture2D;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class MapTileSheet extends MapTile{
    
    private final int numberOfRows, numberOfCols;

    public MapTileSheet(int numberOfRows, int numberOfCols, int id,
            Texture2D texture) {
        super(id, texture);
        this.numberOfRows = numberOfRows;
        this.numberOfCols = numberOfCols;
    }

    /**
     * @return the numberOfRows
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @return the numberOfCols
     */
    public int getNumberOfCols() {
        return numberOfCols;
    }

    public Vector2f getOffset(int index) {
        Vector2f offset = new Vector2f(
            (float) (index % numberOfRows) / numberOfRows,
            (float) (index / numberOfRows) / numberOfRows
        );
        return offset;
    }
    
    
}
