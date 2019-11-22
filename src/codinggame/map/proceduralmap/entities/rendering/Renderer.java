/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.map.renderer.g3d.entities.EntityModel;
import com.lwjglwrapper.opengl.shaders.Shader;

/**
 *
 * @author Welcome
 * @param <T>
 */
public interface Renderer<T> {
    public void render(Shader shader, EntityModel model, T entity);
}
