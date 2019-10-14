/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.tileobjs;

import codinggame.globjs.RenderableTexture;
import codinggame.states.GameState;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.utils.geom.shapes.Rect;

/**
 *
 * @author Welcome
 */
public class TileObject {
    
    private Texture2D texture;
    private int x, y;

    public TileObject(Texture2D texture, int x, int y) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }
    
    public void update(float delta) {
    
    }
    
    public void render() {
//        new RenderableTexture(Rect.jomlRect((x - 0.5f) * GameState.TILE_PIXEL_WIDTH,
//                (y - 0.5f) * GameState.TILE_PIXEL_HEIGHT, GameState.TILE_PIXEL_WIDTH,
//                GameState.TILE_PIXEL_HEIGHT), texture).render();
    }
    
}
