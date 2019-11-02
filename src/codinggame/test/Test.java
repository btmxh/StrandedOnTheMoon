/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.test;

import com.lwjglwrapper.display.Loop;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.utils.colors.StaticColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class Test extends Loop{

    private Shader shader;
    private VAO vao;
    
    @Override
    protected void init() throws Exception {
        super.init();
        shader = new Shader(ShaderFile.fromResource(Test.class, "vert.glsl", GL20.GL_VERTEX_SHADER), ShaderFile.fromResource(Test.class, "frag.glsl", GL20.GL_FRAGMENT_SHADER));
        vao = new VAO();
        vao.bind();
        vao.createAttribute(0, new float[]{-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f}, 2);
        vao.unbind();
    }
    
    @Override
    protected void render() throws Exception {
        super.render();
        GLCalls.setClearColor(StaticColor.BLACK);
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT);
        
        shader.bind();
        vao.bindAll();
        vao.renderArray(GL11.GL_TRIANGLE_STRIP, 0, 4);
        vao.unbindByLastBind();
        shader.unbind();
    }
    
    public static void main(String[] args) {
        new Test().run();
    }
    
}
