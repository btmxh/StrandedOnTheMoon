/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

/**
 *
 * @author Welcome
 */
public class Type {
    
    public static class ClassType extends Type {
        private Class c;

        public ClassType(Class c) {
            this.c = c;
        }

        public Class getClassType() {
            return c;
        }
        
    }
    
    public static class Array extends Type {
        private Type elementType;

        public Array(Type elementType) {
            this.elementType = elementType;
        }

        public Type getElementType() {
            return elementType;
        }
        
        
    }
}
