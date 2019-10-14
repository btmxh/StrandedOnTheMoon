/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map;

import codinggame.map.tiledmap.*;
import com.lwjglwrapper.utils.math.MathUtils;

/**
 *
 * @author Welcome
 */
public interface MapCell {

    public MapTile getTileType();
    public void setTileType(MapTile tile);
    public MapCell clone();
    public void updateSavedData(MapTilesets tilesets);
    public int getBreakingStage();
    public void increaseBreakingProgress(float deltabp);
    public boolean isBreaking();
    
}
