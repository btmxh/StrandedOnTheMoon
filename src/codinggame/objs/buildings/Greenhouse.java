/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.buildings;

import codinggame.map.MapCell;
import codinggame.map.MapTile;
import codinggame.map.MapTilesets;
import codinggame.map.cells.ColoredCell;
import codinggame.map.proceduralmap.ProcMapCell;
import com.lwjglwrapper.utils.colors.StaticColor;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class Greenhouse extends Building{
    
    public Greenhouse(Vector2f position, int width, int height) {
        super(position, width, height);
    }

    @Override
    public void setTiles(MapTilesets tilesets) {
        super.setTiles(tilesets);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapCell cell;
                if(x == 0 | x == width - 1 | y == 0 | y == height - 1) {
                    cell = new ProcMapCell(tilesets.getTileByID(MapTile.BARRIER));
                } else {
                    cell = new ColoredCell(StaticColor.LIME.alpha(0.1f), tilesets.getTileByID(MapTile.GLASS));
                }
                setTileAt(x, y, cell);
            }
        }
    }
    
    
    
}
