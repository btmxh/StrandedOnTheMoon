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
import codinggame.map.proceduralmap.ProcMapLayer;
import codinggame.map.proceduralmap.ProcMapLoader;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.objs.Clock;
import codinggame.objs.buildings.Building;
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

/**
 *
 * @author Welcome
 */
public class MapHandler {

    private final GameState game;
    private GameMap map;
    //private MapRenderer renderer;
    private TileUpdateHandler tileUpdateHandler;
    
    public MapHandler(GameState game, Clock clock) {
        this.game = game;
        try {
//            map = TiledMapLoader.loadMap("", "new_moon.tmx");
            map = ProcMapLoader.loadMap("saves/procmap", "", clock);
            clock.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        this.renderer = new MapRenderer();
//        renderer.setOriginPosition(-1, -1);
        tileUpdateHandler = new TileUpdateHandler(game, this);
    }
    
    public void update() {
        //tileUpdateHandler.update();
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
        BreakTile breakTile = new BreakTile(mapLayer, x, y);
        if(MapCell.nullCheck(cell)) {
            return null;
        } else {
            breakTile(tool, cell.getTileID(), breakTile);
            return null;
        }
    }
    
    public BreakEntity breakTileEntity(Item equipment, int x, int y) {
        MapLayer layer = map.getMapLayer(GameMap.TURF_LAYER);
        BreakEntity b = new BreakEntity(layer, x, y);
        if(layer instanceof ProcMapLayer) {
            EntityData entity = ((ProcMapLayer) layer).getEntityAt(x, y);
            if(entity == null)  return null;
            if(entity.isTileEntity()) {
                int id = entity.getTileID();
                breakTile(equipment, id, b);
                return b;
            } else return null;
        } else throw new UnsupportedOperationException();
    }
    
    private void breakTile(Item tool, int id, Break b) {
        switch (id) {
            case MapTile.COPPER_ORE:
                if(tool instanceof Drill) {
                    b.setItems(new MassItem(ItemTypes.COPPER_ORE, rand(10, 20)));
                }   break;
            case MapTile.MOON_ROCK:
                if(tool instanceof Drill) {
                    b.setItems(new MassItem(ItemTypes.MOON_ROCK, 16));
                }   break;
            case MapTile.ICE:
                if(tool instanceof Drill) {
                    b.setItems(new MassItem(ItemTypes.ICE, 3f));
                }   break;
            case MapTile.IRON_ORE:
                if(tool instanceof Drill) {
                    b.setItems(new MassItem(ItemTypes.IRON_ORE, rand(10, 15)));
                }   break;
            default:
                break;
        }
    }

    private static double rand(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public void renderBuilding(Building building, Camera camera) {
        //renderer.renderBuilding(this, building, camera);
    }
    
    public static class BreakEntity extends Break{
        
        public BreakEntity(MapLayer layer, int x, int y) {
            super(layer, x, y);
        }

        @Override
        public void destroy() {
            if(layer instanceof ProcMapLayer) {
                ((ProcMapLayer) layer).setTileEntityAt(x, y, null, null);
            } else throw new UnsupportedOperationException();
        }
        
        @Override
        public BreakEntity setItems(Item... items) {
            return (BreakEntity) super.setItems(items); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BreakEntity setItems(List<Item> items) {
            return (BreakEntity) super.setItems(items); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public static class BreakTile extends Break {
        
        public BreakTile(MapLayer layer, int x, int y) {
            super(layer, x, y);
        }

        @Override
        public void destroy() {
            layer.setTileAt(x, y, null);
        }

        @Override
        public BreakTile setItems(Item... items) {
            return (BreakTile) super.setItems(items); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public BreakTile setItems(List<Item> items) {
            return (BreakTile) super.setItems(items); //To change body of generated methods, choose Tools | Templates.
        }
        
        
        
    }
    
    public static abstract class Break {
        protected List<Item> items;
        protected MapLayer layer;
        protected int x, y;

        public Break(MapLayer layer, int x, int y) {
            this.items = items;
            this.layer = layer;
            this.x = x;
            this.y = y;
        }
        
        public abstract void destroy();

        public List<Item> getItems() {
            return items;
        }

        public Break setItems(List<Item> items) {
            this.items = items;
            return this;
        }
        
        public Break setItems(Item... items) {
            this.items = Arrays.asList(items);
            return this;
        }
    }
    
}
