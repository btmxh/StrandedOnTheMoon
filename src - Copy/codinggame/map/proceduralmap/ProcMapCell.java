/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import com.lwjglwrapper.utils.math.MathUtils;
import java.io.Serializable;

/**
 *
 * @author Welcome
 */
public class ProcMapCell implements MapCell, Serializable{

    protected MapTile tile;
    protected float breakingProgress = 0;

    public ProcMapCell(MapTile tile) {
        this.tile = tile;
    }
    
    @Override
    public MapTile getTileType() {
        return tile;
    }

    @Override
    public void setTileType(MapTile tile) {
        this.tile = tile;
    }
    
    @Override
    public int getBreakingStage() {
        if(!isBreaking())    return -1;
        else return MathUtils.clamp(0, (int) (breakingProgress * 4), 3);
    }

    @Override
    public void increaseBreakingProgress(float deltabp) {
        this.breakingProgress += deltabp;
    }

    @Override
    public boolean isBreaking() {
        return breakingProgress > 0;
    }

    @Override
    public ProcMapCell clone() {
        ProcMapCell cell = new ProcMapCell(tile);
        cell.breakingProgress = breakingProgress;
        return cell;
    }

    @Override
    public void updateSavedData(MapTilesets tilesets) {
        tile = tilesets.getTileByID(tile.getID());
    }
    
    
    
}
