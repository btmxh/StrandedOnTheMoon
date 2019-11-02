/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.postprocessing;

import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.VAO;
import com.lwjglwrapper.opengl.shaders.Shader;

/**
 *
 * @author Welcome
 */
public class FBORenderer {
    public void renderFBO(VAO vao, Texture2D fboTexture, Shader shader) {
        vao.bindAll();
        fboTexture.bind(0);
        shader.bind();
    }
    
}
