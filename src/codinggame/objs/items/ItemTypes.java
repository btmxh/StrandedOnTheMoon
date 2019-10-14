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
        IRON_DRILL = new Drill.Type(loadTexture("iron_drill"), "Iron Drill", 1);
        COPPER_DRILL = new Drill.Type(loadTexture("copper_drill"), "Copper Drill", 1.25f);
        TITANIUM_DRILL = new Drill.Type(loadTexture("titanium_drill"), "Titanium Drill", 1.5f);
        MOON_ROCK = create("moon_rock", "Moon Rock");
    }
    
    private static ItemType.Mass create(String path, String name) {
        return new ItemType.Mass(loadTexture(path), name);
    }
    
    private static ItemType.Count create(String path, String name, double massPerItem) {
        return new ItemType.Count(loadTexture(path), name, massPerItem);
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
    
    private static Texture2D loadTexture(String path) {
        return new Texture2D(TextureData.fromResource(ItemTypes.class, "/items/" + path +".png")){
            @Override
            public void configTexture(int id) {
                GL13.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL13.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }
        };
    }
}
