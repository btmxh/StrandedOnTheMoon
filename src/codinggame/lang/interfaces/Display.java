/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang.interfaces;

import codinggame.CodingGame;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Viewport;

/**
 *
 * @author Welcome
 */
public class Display {
    public static void setSize(int width, int height) {
        CodingGame.getInstance().execInMainThread(() -> {
            LWJGL.window.setSize(width, height);
            CodingGame.getInstance().gs.windowViewport = new Viewport(LWJGL.window);
        });
    }
}
