/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.CodingGame;
import codinggame.globjs.Camera;
import codinggame.map.GameMap;
import codinggame.map.MapCell;
import codinggame.map.MapLayer;
import codinggame.map.MapTile;
import codinggame.map.proceduralmap.ProcMap;
import codinggame.map.proceduralmap.ProcMapChunk;
import codinggame.map.proceduralmap.ProcMapLoader;
import codinggame.map.renderer.MapRenderer;
import codinggame.objs.Clock;
import codinggame.objs.buildings.Building;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemTypes;
import codinggame.objs.items.MassItem;
import codinggame.objs.items.equipments.Drill;
import codinggame.states.GameState;
import com.lwjglwrapper.LWJGL;
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
    private TileUpdateHandler tileUpdateHandler;
    
    public MapHandler(GameState game, Clock clock) {
        this.game = game;
        try {
//            map = TiledMapLoader.loadMap("", "new_moon.tmx");
            map = ProcMapLoader.loadMap("saves/procmap", "", clock);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.renderer = new MapRenderer();
        renderer.setOriginPosition(-1, -1);
        tileUpdateHandler = new TileUpdateHandler(game, this);
    }
    
    public void update(Camera camera) {
        Vector2f glTranslation = camera.getGLTranslation(game.getMapViewport());
        renderer.setOriginPosition(-1f + glTranslation.x, -1f - glTranslation.y);
        renderer.setTileSize(game.getMapViewport(), game.getCamera().getTileSize(), game.getCamera().getTileSize());
        
        if(map instanceof ProcMap) {
            Vector2f centerTile = camera.getPixelTranslation().negate().add(LWJGL.window.getWidth() / 2, 0).mul(1/camera.getTileSize());
            int chunkX = (int) Math.floor(centerTile.x / ProcMapChunk.CHUNK_SIZE);
            int chunkY = (int) -Math.floor(centerTile.y / ProcMapChunk.CHUNK_SIZE);

            chunkX = 0;
            chunkY = 0;
            ((ProcMap) map).update(chunkX, chunkY);
        }
        tileUpdateHandler.update();
    }
    
    public void render(boolean notHoveringOnRobots) {
        renderer.render(this, map, game.getCamera(), notHoveringOnRobots);
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
    
    public BreakTile breakTile(Item tool, int layer, int x, int y) {
        MapLayer mapLayer = map.getMapLayer(layer);
        MapCell cell = mapLayer.getTileAt(x, y);
        MapTile tile;
        BreakTile breakTile = new BreakTile(mapLayer, x, y);
        if(cell == null? true:(tile = cell.getTileType()) == null) {
            return null;
        } else {
            switch (tile.getID()) {
                case MapTile.COPPER_ORE:
                    if(tool instanceof Drill) {
                        return breakTile.setItems(new MassItem(ItemTypes.COPPER_ORE, rand(10, 20)));
                    }   break;
                case MapTile.MOON_ROCK:
                    if(tool instanceof Drill) {
                        return breakTile.setItems(new MassItem(ItemTypes.MOON_ROCK, 16));
                    }   break;
                case MapTile.ICE:
                    if(tool instanceof Drill) {
                        return breakTile.setItems(new MassItem(ItemTypes.ICE, 3f));
                    }   break;
                case MapTile.IRON_ORE:
                    if(tool instanceof Drill) {
                        return breakTile.setItems(new MassItem(ItemTypes.IRON_ORE, rand(10, 15)));
                    }   break;
                default:
                    break;
            }
            return null;
        }
    }

    private static double rand(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public void renderBuilding(Building building, Camera camera) {
        renderer.renderBuilding(this, building, camera);
    }
    //Choosing
    public static class ChooseTile {
        public final int x, y;
        public final Object layerOrBuilding;

        public ChooseTile(int x, int y, Object layerOrBuilding) {
            this.x = x;
            this.y = y;
            this.layerOrBuilding = layerOrBuilding;
        }
        
        public MapCell getCell() {
            if (layerOrBuilding instanceof MapLayer) {
                MapLayer layer = (MapLayer) layerOrBuilding;
                return layer.getTileAt(x, y);
            } else if(layerOrBuilding instanceof Building) {
                Building building = (Building) layerOrBuilding;
                return building.getTileAt(x, y);
            } else throw new RuntimeException();
        }
        
    }
    public ChooseTile chooseTile;
    
    public void chooseTile(Object layerOrBuilding, int x, int y) {
        if(tileChoosen(layerOrBuilding, x, y))  return;
        chooseTile = new ChooseTile(x, y, layerOrBuilding);
        GameState game = CodingGame.getInstance().gs;
        game.select(chooseTile);
    }
    
    public boolean tileChoosen(Object layerOrBuilding, int x, int y) {
        if(CodingGame.getInstance().gs.getSelectedObject() instanceof ChooseTile) 
            return chooseTile == null? false:chooseTile.x == x && chooseTile.y == y && chooseTile.layerOrBuilding == layerOrBuilding;
        else return false;
    }
    
    public static class BreakTile {
        private List<Item> items;
        private MapLayer layer;
        private int x, y;

        public BreakTile(MapLayer layer, int x, int y) {
            this.items = items;
            this.layer = layer;
            this.x = x;
            this.y = y;
        }
        
        public void destroy() {
            layer.setTileAt(x, y, (MapTile) null);
        }

        public List<Item> getItems() {
            return items;
        }

        private BreakTile setItems(List<Item> items) {
            this.items = items;
            return this;
        }
        
        private BreakTile setItems(Item... items) {
            this.items = Arrays.asList(items);
            return this;
        }
    }
    
}
