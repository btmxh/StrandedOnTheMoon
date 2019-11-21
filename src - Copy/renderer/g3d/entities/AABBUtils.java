/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.renderer.g3d.entities;

import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 *
 * @author Welcome
 */
public class AABBUtils {
    public static class AABBBuilder extends AABBf{

        public AABBBuilder(float minX, float minY, float minZ, float maxX,
                float maxY, float maxZ) {
            super(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public AABBBuilder() {
        }
        
        public AABBBuilder(AABBf source) {
            super(source);
        }
        
        public AABBBuilder(Vector3fc min, Vector3fc max) {
            super(min, max);
        }
        
        public AABBBuilder xCenter(float x, float side) {
            minX = x - side / 2;
            maxX = x + side / 2;
            return this;
        }
        
        public AABBBuilder xCorner(float x, float side) {
            minX = x;
            maxX = x + side;
            return this;
        }
        
        public AABBBuilder yCenter(float y, float side) {
            minY = y - side / 2;
            maxY = y + side / 2;
            return this;
        }
        
        public AABBBuilder yCorner(float y, float side) {
            minY = y;
            maxY = y + side;
            return this;
        }
        
        public AABBBuilder zCenter(float z, float side) {
            minZ = z - side / 2;
            maxZ = z + side / 2;
            return this;
        }
        
        public AABBBuilder zCorner(float z, float side) {
            minZ = z;
            maxZ = z + side;
            return this;
        }
        
        public AABBf build() {
            return new AABBf(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
    
    public static AABBf translate(AABBf aabb, Vector3f translation) {
        AABBf clone = new AABBf(aabb);
        clone.minX += translation.x;
        clone.minY += translation.y;
        clone.minZ += translation.z;
        clone.maxX += translation.x;
        clone.maxY += translation.y;
        clone.maxZ += translation.z;
        return clone;
    }
}
