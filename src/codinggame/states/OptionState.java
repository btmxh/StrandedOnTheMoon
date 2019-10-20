/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import codinggame.globjs.RenderableTexture;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.states.State;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Welcome
 */
public class OptionState extends State<CodingGame>{

    public OptionState(CodingGame game) {
        super(game);
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
        new RenderableTexture(new Rectanglef(0, 0, LWJGL.window.getWidth(), LWJGL.window.getHeight()), game.getGameState().gameTexture).render(true);
        LWJGL.graphics.begin();
        LWJGL.graphics.rect(100, 200, 300, 400);
        LWJGL.graphics.fill(IColor.RED);
        LWJGL.graphics.end();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
    
}
