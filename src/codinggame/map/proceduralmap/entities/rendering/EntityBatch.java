/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.map.renderer.g3d.entities.EntityModel;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class EntityBatch {
    private EntityModel model;
    private ForEachable subBatches;

    public EntityBatch(EntityModel model, ForEachable subBatches) {
        this.model = model;
        this.subBatches = subBatches;
    }

    public ForEachable getSubBatches() {
        return subBatches;
    }

    public EntityModel getModel() {
        return model;
    }
    
    
}
