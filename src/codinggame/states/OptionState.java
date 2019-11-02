/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import codinggame.globjs.RenderableTexture;
import codinggame.handlers.GameUIHandler;
import codinggame.postprocessing.GaussianBlur;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.utils.Utils;
import com.lwjglwrapper.utils.colors.StaticColor;
import com.lwjglwrapper.utils.states.State;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class OptionState extends State<CodingGame>{

    private GaussianBlur blur;
    private NVGImage rocket;
    
    public OptionState(CodingGame game) {
        super(game);
        blur = new GaussianBlur();
        try {
            rocket = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(OptionState.class.getResourceAsStream("/icons/rocket.png"), 8 * 1024), 0);
        } catch (IOException ex) {
            Logger.getLogger(OptionState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void update(float delta) {
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_ESCAPE)) {
            game.setStateByIndex(0);
        }
    }

    @Override
    public void render() {
        blur.render(game.gs.gameTexture);
        
        LWJGL.graphics.begin();
        LWJGL.graphics.image(rocket, 100, 200, 32, 32);
        LWJGL.graphics.setUpText(GameUIHandler.textFont, StaticColor.WHITE, 32, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE);
        LWJGL.graphics.text("Resume", 150, 200);
        LWJGL.graphics.end();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        rocket.dispose();
    }
    
}
