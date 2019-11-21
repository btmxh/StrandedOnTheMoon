/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d.entities;

import codinggame.map.MapTile;
import codinggame.map.renderer.g3d.entities.AABBUtils.AABBBuilder;
import codinggame.map.tiles.MapTiles;
import codinggame.objs.robots.Robot;
import codinggame.utils.INoise;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.utils.math.MathUtils;
import com.lwjglwrapper.utils.models.ModelGenerator;
import com.lwjglwrapper.utils.models.OBJLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Welcome
 */
public class Entity {
    private static Map<String, EntityModel> models;
    
    static {
        models = new HashMap<>();
        OBJLoader loader = new OBJLoader(0, 2, 1);
        ModelGenerator mg = new ModelGenerator(loader);
        try {
            models.put(Robot.class.getSimpleName(), create(loader, "/models/stall/stall.obj", "/models/stall/stallTexture.png", new AABBBuilder().xCenter(0, 0.4f).yCorner(0, 0.5f).zCenter(0, 0.2f).build()));
            models.put(MapTiles.get(MapTile.COPPER_ORE).getName(), new EntityModel(mg.texturedCube2D(Entity.class, "/tiles/ore_copper_moon.png", 0), new AABBBuilder().xCenter(0f, 1f).yCenter(0f, 1f).zCenter(0f, 1f).build()));
        } catch (IOException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static EntityModel getModel(Class clazz) {
        return models.get(clazz.getSimpleName());
    }
    
    public static EntityModel getModel(String className) {
        return models.get(className);
    }
    
    private static EntityModel create(OBJLoader loader, String obj, String tex, AABBf boundBox) throws IOException {
        TexturedVAO mesh = loader.loadOBJ(Entity.class.getResourceAsStream(obj), new Texture2D(TextureData.fromResource(Entity.class, tex)), 0, true);
        return new EntityModel(mesh, boundBox);
    }
    
    private static Texture2D loadTex(String filepath) {
        return new Texture2D(TextureData.fromResource(Entity.class, filepath));
    }
    
    protected final EntityModel model;
    protected final Vector3f position, rotation, scale;

    public Entity(EntityModel model) {
        this.model = model;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(0.05f);
    }

    public Entity(EntityModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
    
    public Matrix4f getMatrix() {
        return MathUtils.transformationMatrix(position, rotation, scale);
    }

    public void render() {
        model.getMesh().renderElement(GL11.GL_TRIANGLES, -1);
    }

    public void syncHeight(INoise noise) {
        position.y = noise.getTriangleHeight(position.x, position.z) - model.getBoundBox().minY;
    }

    public TexturedVAO getMesh() {
        return model.getMesh();
    }
    
    public AABBf getBoundBox() {
        return model.getBoundBox(position);
    }

    public void update(INoise noise, float delta) {
        syncHeight(noise);
    }
    
    
}
