/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.tileobjs;

import codinggame.objs.Inventory;
import com.lwjglwrapper.opengl.objects.Texture2D;

/**
 *
 * @author Welcome
 */
public class StorageUnit extends TileObject{
    protected Inventory inventory;

    public StorageUnit(Inventory inventory, Texture2D texture, int x, int y) {
        super(texture, x, y);
        this.inventory = inventory;
    }
}
