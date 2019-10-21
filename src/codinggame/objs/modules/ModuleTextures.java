/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.modules;

import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;

/**
 *
 * @author Welcome
 */
public class ModuleTextures {
    public static Texture2D SPEED_MODULE, SOLAR_PANEL_MODULE, MINING_LUCK_MODULE, MINING_SPEED_MODULE;;
    
    public static void init() {
        SPEED_MODULE = new Texture2D(TextureData.fromResource(ModuleTextures.class, "/items/speed_module.png"));
        SOLAR_PANEL_MODULE = new Texture2D(TextureData.fromResource(ModuleTextures.class, "/items/solar_module.png"));
        MINING_LUCK_MODULE = new Texture2D(TextureData.fromResource(ModuleTextures.class, "/items/mine_luck_module.png"));
        MINING_SPEED_MODULE = new Texture2D(TextureData.fromResource(ModuleTextures.class, "/items/mine_speed_module.png"));
    }
}
