/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.CodingGame;
import codinggame.map.MapTile;
import codinggame.map.renderer.g3d.entities.AABBUtils;
import codinggame.map.renderer.g3d.entities.EntityModel;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.utils.models.ModelGenerator;
import com.lwjglwrapper.utils.models.OBJLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.AABBf;

/**
 *
 * @author Welcome
 */
public class ModelManager {
    private static final Map<String, List<EntityModel>> models = new HashMap<>();
    private static final OBJLoader loader = new OBJLoader(0, 2, 1);
    private static final ModelGenerator mg = new ModelGenerator(loader);
    
    public static final EntityModel ROBOT;
    public static final Map<Integer, EntityModel> TILE_ENTITIES = new HashMap<>();
    
    static {
        ROBOT = putModel("Entity", create("Entity", "/models/stall/stall.obj", "/models/stall/stallTexture.png", new AABBUtils.AABBBuilder().xCenter(0, 0.4f).yCorner(0, 0.5f).zCenter(0, 0.2f).build()));
        
    }
    
    public static EntityModel putModel(String type, EntityModel model) {
        List<EntityModel> list = models.get(type);
        if(list == null) {
            list = new ArrayList<>();
            models.put(type, list);
        }
        list.add(model);
        return model;
    }

    public static EntityModel tileEntityModel(int tileID) {
        EntityModel model = TILE_ENTITIES.get(tileID);
        if(model == null) {
            CodingGame inst = CodingGame.getInstance();
            MapTile tile = inst.gs.getMapHandler().getMap().getTilesets().getTileByID(tileID);
            model = new EntityModel("TileEntity", mg.texturedCube2D(tile.getTexture(), 0), 
                    new AABBUtils.AABBBuilder().xCenter(0f, 1f).yCenter(0f, 1f).zCenter(0f, 1f));
            TILE_ENTITIES.put(tileID, model);
        }
        return model;
    }
    
    private static EntityModel create(String type, String obj, String tex, AABBf boundBox) {
        try {
            TexturedVAO mesh = loader.loadOBJ(ModelManager.class.getResourceAsStream(obj), new Texture2D(TextureData.fromResource(ModelManager.class, tex)), 0, true);
            return new EntityModel(type, mesh, boundBox);
        } catch (IOException ex) {
            Logger.getLogger(ModelManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
