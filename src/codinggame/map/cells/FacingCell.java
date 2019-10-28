/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.cells;

import codinggame.map.MapTile;
import codinggame.map.MapTileset;
import codinggame.map.proceduralmap.ProcMapCell;
import org.joml.Vector4f;

/**
 *
 * @author Welcome
 */
public class FacingCell extends DataCell{

    private Facing facing;
    
    public FacingCell(MapTile tile) {
        super(tile);
        facing = Facing.RIGHT;
    }
    
    public enum Facing {
        LEFT(-1, 0, 0, -1), UP(0, -1, 1, 0), RIGHT(1, 0, 0, 1), DOWN(0, 1, -1, 0);
        private Vector4f matrix;

        private Facing(float a, float b, float c, float d) {
            this.matrix = new Vector4f(a, b, c, d);
        }

        public Vector4f getMatrix() {
            return matrix;
        }
        
    }

    /**
     * @return the facing
     */
    public Facing getFacing() {
        return facing;
    }

    /**
     * @param facing the facing to set
     */
    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    @Override
    public FacingCell clone() {
        FacingCell cell = new FacingCell(tile);
        cell.facing = facing;
        return cell;
    }
    
    
}
