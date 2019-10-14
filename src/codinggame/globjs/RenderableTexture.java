/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.globjs;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.ShaderFile;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UInt;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec2;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 *
 * @author Welcome
 */
public class RenderableTexture {
    private static Shader shader;
    private static VAO vao;
    private static UVec2 utranslation, uscale;
    private static UInt uflipY;
    
    public static void init() {
        shader = new Shader(
            ShaderFile.fromResource(RenderableTexture.class, "shaders/rtvert.glsl", GL20.GL_VERTEX_SHADER),
            ShaderFile.fromResource(RenderableTexture.class, "shaders/rtfrag.glsl", GL20.GL_FRAGMENT_SHADER)
        );
        utranslation = new UVec2(shader, "translation");
        uscale = new UVec2(shader, "scale");
        uflipY = new UInt(shader, "flipY");
        vao = new VAO();
        vao.bind();
        vao.createAttribute(0, new float[]{0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0}, 2);
        vao.unbind();
    }
    
    private Vector2f translation;
    private Vector2f scale;
    private Texture2D texture;

    public RenderableTexture(Rectanglef bounds, Texture2D texture) {
        this.texture = texture;
        this.setBounds(bounds);
    }

    public final void setBounds(Rectanglef bounds) {
        float x = bounds.minX / LWJGL.window.getWidth() * 2 - 1;
        float y = 1 - bounds.minY / LWJGL.window.getHeight() * 2;
        float w = (bounds.maxX - bounds.minX) / LWJGL.window.getWidth() * 2;
        float h = (bounds.maxY - bounds.minY) / LWJGL.window.getHeight()* 2;
        translation = new Vector2f(x, y - h);
        scale = new Vector2f(w, h);
    }
    
    public void render() {
        shader.bind();
        utranslation.load(translation);
        uscale.load(scale);
        texture.bind(0);
        vao.bindAll();
        vao.renderArray(GL11.GL_TRIANGLES, 0, 6);
        vao.unbindByLastBind();
        texture.unbind();
        shader.unbind();
    }

    public void render(boolean flipY) {
        shader.bind();
        utranslation.load(translation);
        uscale.load(scale);
        uflipY.load(flipY? 1:0);
        texture.bind(0);
        vao.bindAll();
        vao.renderArray(GL11.GL_TRIANGLES, 0, 6);
        vao.unbindByLastBind();
        texture.unbind();
        shader.unbind();
    }
}
