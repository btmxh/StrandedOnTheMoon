/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.states;

import codinggame.CodingGame;
import com.lwjglwrapper.opengl.GLCalls;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.states.State;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Welcome
 */
public class MenuState extends State<CodingGame>{

    public MenuState(CodingGame game) {
        super(game);
    }

    @Override
    public void show() {
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render() {
        GLCalls.setClearColor(new IColor(0.8f));
        GLCalls.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
    
}
