/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.map.proceduralmap.entities.rendering;

import codinggame.handlers.GameHandler;
import com.lwjglwrapper.utils.cameras.Camera;

/**
 *
 * @author Welcome
 */
public class EntityHandler {
    private EntityRenderers renderer;
    private EntityCollection collection;

    public EntityHandler(GameHandler game) {
        collection = new EntityCollection();
        renderer = new EntityRenderers(game);
    }

    public EntityCollection getEntities() {
        return collection;
    }

    public void render(Camera camera) {
        renderer.renderBatch(collection, camera);
    }
    
    
    
    
}
