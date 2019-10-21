/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.globjs.Camera;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMap;
import codinggame.map.proceduralmap.ProcMapLoader;
import codinggame.map.renderer.MapRenderer;
import codinggame.map.tiledmap.TiledMapLoader;
import codinggame.objs.Clock;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.items.equipments.Drill;
import codinggame.states.GameState;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2f;

/**
 *
 * @author Welcome
 */
public class MapHandler {

    private final GameState game;
    private GameMap map;
    private MapRenderer renderer;
    
    public MapHandler(GameState game, Clock clock) {
        this.game = game;
        try {
            map = TiledMapLoader.loadMap("", "new_moon.tmx");
//            map = ProcMapLoader.loadMap("saves/procmap", "", clock);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.renderer = new MapRenderer();
        renderer.setOriginPosition(-1, -1);
        
    }
    
    public void update(Camera camera) {
        Vector2f glTranslation = camera.getGLTranslation(game.getMapViewport());
        renderer.setOriginPosition(-1f + glTranslation.x, -1f - glTranslation.y);
        renderer.setTileSize(game.getMapViewport(), game.getCamera().getTileSize(), game.getCamera().getTileSize());
        
        if(map instanceof ProcMap) {
            ((ProcMap) map).update();
        }
    }
    
    public void render(boolean notHoveringOnRobots) {
        renderer.render(map, game.getCamera(), notHoveringOnRobots);
    }
    
    public void save(Clock clock) {
        if(map instanceof ProcMap) {
            try {
                ((ProcMap) map).save(clock);
            } catch (IOException ex) {
                Logger.getLogger(MapHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public GameMap getMap() {
        return map;
    }
    
    public List<Item> breakTile(Item tool, int layer, int x, int y) {
        MapLayer mapLayer = map.getMapLayer(layer);
        MapCell cell = mapLayer.getTileAt(x, y);
        MapTile tile;
        if(cell == null? true:(tile = cell.getTileType()) == null) {
            return null;
        } else {
            if(tile.getID() == MapTile.COPPER_ORE) {
                if(tool instanceof Drill) {
                    mapLayer.setTileAt(x, y, (MapTile) null);
                    return Arrays.asList(new MassItem(ItemTypes.COPPER_ORE, rand(10, 20)));
                }
            } else if(tile.getID() == MapTile.MOON_ROCK) {
                if(tool instanceof Drill) {
                    mapLayer.setTileAt(x, y, (MapTile) null);
                    return Arrays.asList(new MassItem(ItemTypes.MOON_ROCK, 16));
                }
            }
            return null;
        }
    }

    private static double rand(double min, double max) {
        return Math.random() * (max - min) + min;
    }
    
}
