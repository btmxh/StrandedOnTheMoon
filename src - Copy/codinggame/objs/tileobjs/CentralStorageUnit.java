/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.objs.tileobjs;

import codinggame.objs.Inventory;
import com.lwjglwrapper.opengl.objects.Texture2D;
import com.lwjglwrapper.opengl.objects.TextureData;

/**
 *
 * @author Welcome
 */
public class CentralStorageUnit extends StorageUnit{
    
    public CentralStorageUnit(int x, int y) {
        super(new Inventory(500), texture(), x, y);
    }
    
    private static Texture2D texture;
    private static final Texture2D texture() {
        if(texture != null) return texture;
        return new Texture2D(TextureData.fromResource(CentralStorageUnit.class, "/tiles/central_storage_unit.png"));
    }
}
