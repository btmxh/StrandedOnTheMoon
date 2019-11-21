/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d.entities.renderer;

import codinggame.map.renderer.g3d.entities.Entity;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UMat4;
import com.lwjglwrapper.utils.cameras.Camera;
import java.util.Map;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class EntityRenderer {

    private final Shader shader;
    private final UMat4 tMatrix;
    private final UMat4 pvMatrix;

    public EntityRenderer() {
        shader = new Shader(
                ShaderFile.fromResource(EntityRenderer.class, "vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(EntityRenderer.class, "fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        tMatrix = new UMat4(shader, "tMatrix");
        pvMatrix = new UMat4(shader, "pvMatrix");
    }
    
    public void render(Camera camera, Entity e) {
        shader.bind();
        tMatrix.load(e.getMatrix());
        pvMatrix.load(camera.getCombinedMatrix());
        e.getMesh().bindAll();
        e.render();
        e.getMesh().unbindByLastBind();
        shader.unbind();
    }
    
    public void render(Camera camera, Map<TexturedVAO, ? extends Iterable<? extends Entity>> entities) {
        shader.bind();
        pvMatrix.load(camera.getCombinedMatrix());
        for (TexturedVAO model : entities.keySet()) {
            model.bindAll();
            Iterable<? extends Entity> batch = entities.get(model);
            for (Entity e : batch) {
                tMatrix.load(e.getMatrix());
                e.render();
            }
            model.unbindByLastBind();
        }
        shader.unbind();
    }
    
}
