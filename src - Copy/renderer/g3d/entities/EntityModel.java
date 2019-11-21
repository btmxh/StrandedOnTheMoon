/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d.entities;

import com.lwjglwrapper.opengl.objects.TexturedVAO;
import org.joml.AABBf;
import org.joml.Vector3f;

/**
 *
 * @author Welcome
 */
public class EntityModel {
    private TexturedVAO vao;
    private AABBf boundBox;

    public EntityModel(TexturedVAO vao, AABBf boundBox) {
        this.vao = vao;
        this.boundBox = boundBox;
    }

    public TexturedVAO getMesh() {
        return vao;
    }

    public AABBf getBoundBox() {
        return boundBox;
    }

    public AABBf getBoundBox(Vector3f translation) {
        
        return AABBUtils.translate(boundBox, translation);
    }
    
    
    
    
    
}
