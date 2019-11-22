/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.map.renderer.g3d.entities.EntityModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Welcome
 */
public class EntityCollection {

    public List<ForEachable<EntityBatch>> entities;   //Object may be one of ForEachable or Iterable
    private final Map<EntityModel, List> batchMap;
    
    public EntityCollection() {
        entities = new LinkedList<>();
        this.batchMap = new HashMap<>();
        entities.add(foreachable(batchMap));
    }
    
    public void put(EntityModel model, Iterable entityIterable) {
        List batch = batchMap.get(model);
        if(batch == null) {
            batch = new LinkedList();
            batchMap.put(model, batch);
        }
        batch.add(entityIterable);
    }
    
    public void put(EntityModel model, ForEachable entityIterable) {
        List batch = batchMap.get(model);
        if(batch == null) {
            batch = new LinkedList();
            batchMap.put(model, batch);
        }
        batch.add(entityIterable);
    }
    
    public <E> void putOne(EntityModel model, E entity) {
        ForEachable<E> forEachable = (c) -> c.accept(entity);
        put(model, forEachable);
    }

    public static ForEachable<EntityBatch> foreachable(Map<EntityModel, List> batchMap) {
        return (c) -> {
            for (Map.Entry<EntityModel, List> entry : batchMap.entrySet()) {
                EntityBatch b = new EntityBatch(entry.getKey(), (consumer) -> {
                    for (Object aBatch : entry.getValue()) {
                        ForEachable.forEach(aBatch, consumer);
                    }
                });
                c.accept(b);
            }
        };
    }
    
    public void put(ForEachable<EntityBatch> batch) {
        entities.add(batch);
    }
    
    
}
