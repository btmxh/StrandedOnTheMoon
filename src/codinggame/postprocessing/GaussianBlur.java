/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.postprocessing;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.objects.FBO;
import com.lwjglwrapper.opengl.objects.RBO;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UFloat;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec2;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author Welcome
 */
public class GaussianBlur {
    private VAO vao;
    private Shader horizontalBlur, verticalBlur;
    private UVec2 h_translation, h_scale;
    private UFloat h_fboWidth, v_fboHeight;
    private UVec2 v_translation, v_scale;
    
    private FBO fbo;
    private RBO rbo;
    private Texture2D fboTexture;
    
    private boolean resizeFBO = false;

    public GaussianBlur() {
        horizontalBlur = new Shader(
            ShaderFile.fromResource(GaussianBlur.class, "/codinggame/postprocessing/blur/hor_vertex.glsl", GL20.GL_VERTEX_SHADER),
            ShaderFile.fromResource(GaussianBlur.class, "/codinggame/postprocessing/blur/fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        h_translation = new UVec2(horizontalBlur, "translation");
        h_scale = new UVec2(horizontalBlur, "scale");
        h_fboWidth = new UFloat(horizontalBlur, "fboWidth");
        
        verticalBlur = new Shader(
            ShaderFile.fromResource(GaussianBlur.class, "/codinggame/postprocessing/blur/ver_vertex.glsl", GL20.GL_VERTEX_SHADER),
            ShaderFile.fromResource(GaussianBlur.class, "/codinggame/postprocessing/blur/fragment.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        v_translation = new UVec2(verticalBlur, "translation");
        v_scale = new UVec2(verticalBlur, "scale");
        v_fboHeight = new UFloat(verticalBlur, "fboHeight");
        
        vao = new VAO();
        vao.bind();
        vao.createAttribute(0, new float[]{-1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1}, 2);
        vao.unbind();
        
        LWJGL.windowCallbacks.getSizeCallback().add(new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resizeFBO = true;
            }
        });
        
        resizeFBO(LWJGL.window.getWidth(), LWJGL.window.getHeight());
    }
    
    public void resizeFBO(int width, int height) {
        if(fbo != null) fbo.dispose();
        if(rbo != null) rbo.dispose();
        fbo = new FBO(GL30.GL_FRAMEBUFFER);
        fbo.bind();
        rbo = new RBO(GL30.GL_DEPTH24_STENCIL8, width, height);
        fbo.attachRBO(GL30.GL_DEPTH_STENCIL_ATTACHMENT, rbo);
        fboTexture = fbo.createTexture();
        fbo.checkProgress(System.out, true);
        fbo.unbind();
        resizeFBO = false;
    }
    
    public void render(Texture2D texture) {
        if(resizeFBO)   resizeFBO(LWJGL.window.getWidth(), LWJGL.window.getHeight());
        GL11.glViewport(0, 0, LWJGL.window.getWidth(), LWJGL.window.getHeight());
        vao.bindAll();
        texture.bind(0);
        fbo.bind();
        horizontalBlur.bind();
        h_translation.load(-1f, -1f);
        h_scale.load(2, 2);
        h_fboWidth.load(LWJGL.window.getWidth());
        vao.renderArray(GL11.GL_TRIANGLES, 0, 6);
        horizontalBlur.unbind();
        fbo.unbind();
        
        fboTexture.bind(0);
        verticalBlur.bind();
        v_translation.load(-1f, -1f);
        v_scale.load(2, 2);
        v_fboHeight.load(LWJGL.window.getHeight());
        vao.renderArray(GL11.GL_TRIANGLES, 0, 6);
        verticalBlur.unbind();
        
        vao.unbindByLastBind();
    }
    
}
