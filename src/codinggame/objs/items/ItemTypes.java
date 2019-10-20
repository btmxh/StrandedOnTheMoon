/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.items;

import codinggame.objs.items.equipments.Drill;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 *
 * @author Welcome
 */
public class ItemTypes {
    public static ItemType.Mass COPPER_ORE, MOON_ROCK;
    public static ItemType.Count COTEST1, COTEST2;
    public static Drill.Type IRON_DRILL, TITANIUM_DRILL, COPPER_DRILL;
    
    public static void initTypes() {
        COPPER_ORE = create("copper_ore", "Copper Ore");
        COTEST1 = create("copper_ore", "Copper", 10);
        COTEST2 = create("copper_ore", "DONG", 10);
        IRON_DRILL = new Drill.Type("/items/iron_drill.png", "Iron Drill", 1);
        COPPER_DRILL = new Drill.Type("/items/copper_drill.png", "Copper Drill", 1.25f);
        TITANIUM_DRILL = new Drill.Type("/items/titanium_drill.png", "Titanium Drill", 1.5f);
        MOON_ROCK = create("moon_rock", "Moon Rock");
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
