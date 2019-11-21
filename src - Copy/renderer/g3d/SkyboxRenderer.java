/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.objects.TextureCube;
import com.lwjglwrapper.opengl.objects.TexturedVAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UMat4;
import com.lwjglwrapper.utils.cameras.Camera;
import com.lwjglwrapper.utils.models.ModelGenerator;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class SkyboxRenderer {

    private TexturedVAO model;
    private Shader shader;
    private UMat4 pvMatrix;
    private UInt cubeMap;
    private final TextureCube skyboxTexture;
    private float rotation;
    
    public SkyboxRenderer() {
        skyboxTexture = new TextureCube(TextureCube.RESOURCE_MODE, "/skybox", "png");
        model = new ModelGenerator().texturedCube3D(skyboxTexture, 0);
        shader = new Shader(
                ShaderFile.fromResource(SkyboxRenderer.class, "skybox_vertex.glsl", GL20.GL_VERTEX_SHADER),
                ShaderFile.fromResource(SkyboxRenderer.class, "skybox_fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        pvMatrix = new UMat4(shader, "pvMatrix");
        cubeMap = new UInt(shader, "cubeTexture");
        
        shader.bind();
        cubeMap.load(0);
        shader.unbind();
    }
    
    public void render(Camera camera) {
        shader.bind();
        rotation += LWJGL.currentLoop.getDeltaTime() * 0.1f;
        Matrix4f viewMatrix = camera.getViewMatrix();
        viewMatrix.setTranslation(0, 0, 0);
        viewMatrix.rotate((float) Math.toRadians(rotation), 0, 1, 0);
        pvMatrix.load(camera.getProjectionMatrix().mul(viewMatrix));
        model.bindAll();
        skyboxTexture.bind(0);
        cubeMap.load(0);
        model.renderElement(GL11.GL_TRIANGLES, 0, -1);
        model.unbindByLastBind();
        shader.unbind();
    }
    
}
