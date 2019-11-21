/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities;

import codinggame.utils.ArrayMap;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Welcome
 */
public class ObjectTemplate extends Type{

    private ArrayMap<Type, Integer> objectMetas;

    public ObjectTemplate(ArrayMap<Type, Integer> objectMetas) {
        this.objectMetas = objectMetas;
    }
    
    public static class Builder {
        private ArrayMap<Type, Integer> objectMetas;

        public Builder() {
            objectMetas = new ArrayMap<>();
        }

        public Builder(ArrayMap<Type, Integer> objectMetas) {
            this.objectMetas = objectMetas;
        }
        
        public Builder addMeta(Type type, int count) {
            objectMetas.put(type, count);
            return this;
        }
        
        public Builder addMeta(Class c, int count) {
            return addMeta(new ClassType(c), count);
        }
        
        public Builder addArray(Type elementType, int count) {
            return addMeta(new Array(elementType), count);
        }
        
        public Builder addArray(Class elementType, int count) {
            return addArray(new ClassType(elementType), count);
        }
        
        public ObjectTemplate build() {
            return new ObjectTemplate(objectMetas);
        }
        
    }

    public static ObjectTemplate reflection(Class<EntityData> c) {
        Field[] fields = c.getFields();
        Builder builder = new Builder();
        for (Field field : fields) {
            Template annotation = field.getAnnotation(Template.class);
            if(annotation == null) {
                builder.addMeta(field.getType(), 1);
            } else {
                builder.addMeta(getTemplateFromClass(field.getType()), 1);
            }
        }
        return builder.build();
    }
    
    public static ObjectTemplate getTemplateFromClass(Class c) {
        try {
            return (ObjectTemplate) c.getMethod("getTemplate").invoke(null);
        } catch (Exception ex) {
            Logger.getLogger(ObjectTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
