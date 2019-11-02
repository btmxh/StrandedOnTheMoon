/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import codinggame.chemistry.Element;
import codinggame.chemistry.ElementGroup;
import codinggame.chemistry.MultiElementGroup;
import codinggame.objs.items.equipments.Drill;
import codinggame.objs.items.equipments.Hoe;
import codinggame.objs.items.equipments.WateringCan;
import codinggame.objs.modules.Module;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class ItemTypes {
    public static ItemType.Mass COPPER_ORE, IRON_ORE, MOON_ROCK, ICE;
    public static ItemType.Count POTATO_SEED_BAG, POTATO, POTATO_CAN, BUCKET, WATER_BUCKET, WATERING_CAN;
    public static Drill.Type IRON_DRILL, TITANIUM_DRILL, COPPER_DRILL, OLD_DRILL;
    public static Hoe.Type IRON_HOE, TITANIUM_HOE, COPPER_HOE, OLD_HOE;
    
    public static void initTypes() {
        COPPER_ORE = create("copper_ore", "Copper Ore");
        IRON_DRILL = new Drill.Type("/items/iron_drill.png", "Iron Drill", 1);
        COPPER_DRILL = new Drill.Type("/items/copper_drill.png", "Copper Drill", 1.25f);
        TITANIUM_DRILL = new Drill.Type("/items/titanium_drill.png", "Titanium Drill", 1.5f);
        OLD_DRILL = new Drill.Type("/items/old_drill.png", "Old Drill", 0.75f);
        MOON_ROCK = new ItemType.Mass("/items/moon_rock.png", "Moon Rock"){
            private ElementGroup compound;
            
            @Override
            public ElementGroup getCompound() {
                if(compound == null) {
                    compound = new MultiElementGroup().
                            put(Element.O, 0.6f).
                            put(Element.Si, 0.2f).
                            put(Element.Fe, 0.1f).
                            put(Element.Al, 0.05f).
                            put(Element.Ca, 0.05f);
                }
                return compound;
            }
        };
        IRON_HOE = new Hoe.Type("/items/iron_hoe.png", "Iron Hoe", 1);
        COPPER_HOE = new Hoe.Type("/items/copper_hoe.png", "Copper Hoe", 1.25f);
        TITANIUM_HOE = new Hoe.Type("/items/titanium_hoe.png", "Titanium Hoe", 1.5f);
        OLD_HOE = new Hoe.Type("/items/old_hoe.png", "Old Hoe", 0.75f);
        POTATO_SEED_BAG = new ItemType.Count("/items/wheat_seed_bag.png", "Wheat Seed Bag", 2f);
        POTATO = new ItemType.Count("/items/potato.png", "Potato", 5f);
        POTATO_CAN = new ItemType.Count("/items/empty_can.png", "Potato Can", 10f);
        ICE = new ItemType.Mass("/items/ice.png", "Ice Cube"){
            final ElementGroup H2O = new MultiElementGroup().put(Element.H, 0.1f)
                                                            .put(Element.O, 0.9f);
            @Override
            public ElementGroup getCompound() {
                return H2O;
            }
        };
        BUCKET = new ItemType.Count("/items/bucket.png", "Empty Bucket", 3f);
        WATER_BUCKET = new ItemType.Count("/items/water_bucket.png", "Water Bucket", 6f);
        IRON_ORE = new ItemType.Mass("/items/iron_ore.png", "Iron Ore");
        WATERING_CAN = new WateringCan.Type("/items/watering_can.png", "Watering Can", 4f);
    }
    
    private static ItemType.Mass create(String path, String name) {
        return new ItemType.Mass("/items/" + path +".png", name);
    }
    
    private static ItemType.Count create(String path, String name, double massPerItem) {
        return new ItemType.Count("/items/" + path +".png", name, massPerItem);
    }
    
    public static ItemType getItemByName(String name) {
        for (Field field : ItemTypes.class.getFields()) {
            try {
                Object itemType = field.get(null);
                if(itemType instanceof ItemType) {
                    if(((ItemType) itemType).getName().equalsIgnoreCase(name)) {
                        return (ItemType) itemType;
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ItemTypes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException npe) {}
        }
        return null;
    }
    
    public static void dispose() {
        for (Field field : ItemTypes.class.getFields()) {
            try {
                Object val = field.get(null);
                if(val instanceof ItemType) ((ItemType) val).dispose();
            } catch (Exception ex) {
                continue;
            }
        }
    }
}
