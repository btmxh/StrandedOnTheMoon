/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.globjs;

import com.lwjglwrapper.opengl.shaders.Shader;
import com.lwjglwrapper.opengl.shaders.structs.Struct;
import com.lwjglwrapper.opengl.shaders.uniforms.variables.UVec3;
import org.joml.Vector3f;

/**
 *
 * @author Welcome
 */
public class Light {
    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation;

    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
        this.attenuation = new Vector3f(1, 0, 0);
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }
    
    public static class Struct extends com.lwjglwrapper.opengl.shaders.structs.Struct {

        private final UVec3 u_position, u_color, u_attenuation;
        
        public Struct(Shader shader, String variableName) {
            super(shader, variableName);
            u_position = vec3Field("position");
            u_color = vec3Field("color");
            u_attenuation = vec3Field("attenuation");
        }
        
        public void set(Light light) {
            u_position.load(light.position);
            u_color.load(light.color);
            u_attenuation.load(light.attenuation);
        }
        
    }
    
}
