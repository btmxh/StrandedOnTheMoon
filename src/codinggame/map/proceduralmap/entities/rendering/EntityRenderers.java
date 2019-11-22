/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.handlers.GameHandler;
import codinggame.map.proceduralmap.entities.EntityData;
import codinggame.map.renderer.g3d.entities.Entity;
import codinggame.map.renderer.g3d.entities.EntityModel;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.utils.cameras.Camera;
import com.lwjglwrapper.utils.math.MathUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class EntityRenderers {
    
    private GameHandler game;
    private final Shader shader;
    private Map<String, Renderer> renderers;    //Different VAOs could use the same renderer
    
    public final Renderer<Entity> entityRenderer;
    public final Renderer<EntityData> tileEntityRenderer;

    public EntityRenderers(GameHandler game) {
        this.game = game;
        renderers = new HashMap<>();
        
        shader = new Shader(
                ShaderFile.fromResource(EntityRenderers.class, "vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(EntityRenderers.class, "fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        
        entityRenderer = (shader, model, entity) -> {
            shader.loadUniformVariable("tMatrix", entity.getMatrix());
            entity.render();
        };
        tileEntityRenderer = (shader, model, entityData) -> {
            Vector2f pos = new Vector2f(entityData.getPosition()).add(0.5f, 0.5f);
            float height = entityData.getHeight();
            if(Float.isNaN(height)) {
                height = game.getNoise().getTriangleHeight(pos.x, pos.y) + 0.25f;
                entityData.setHeight(height);
            }
            Matrix4f transformationMatrix = MathUtils.transformationMatrix(new Vector3f(pos.x, height, pos.y), new Vector3f(), new Vector3f(1f));
            shader.loadUniformVariable("tMatrix", transformationMatrix);
            model.getMesh().renderElement(GL11.GL_TRIANGLES, -1);
        };
        addRenderer(Entity.class.getSimpleName(), entityRenderer);
        addRenderer("TileEntity", tileEntityRenderer);
    }
    
    public final <T> void addRenderer(String type, Renderer<T> renderer) {
        renderers.put(type, renderer);
    }
    
    //Don't use this method for one-instance rendering! This is for batch rendering
    public <T> void render(String type, EntityModel model, T entity) {
        Renderer renderer = renderers.get(type);
        if(renderer == null)    throw new IllegalArgumentException("Renderer for type " + type + " is not defined");
        renderer.render(shader, model, entity);
    }
    
    public void renderBatch(EntityCollection collection, Camera camera) {
        List<ForEachable<EntityBatch>> entities = collection.entities;
        shader.bind();
        shader.loadUniformVariable("pvMatrix", camera.getCombinedMatrix());
        for (ForEachable<EntityBatch> batches : entities) {
            batches.iterate((batch) -> {
                EntityModel model = batch.getModel();
                model.getMesh().bindAll();
                String type = model.getType();
                ForEachable.forEach(batch.getSubBatches(), (e) -> render(type, model, e));
                model.getMesh().unbindByLastBind();
            });
        }
        shader.unbind();
    }
}
