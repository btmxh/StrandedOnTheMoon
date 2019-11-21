/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import com.lwjglwrapper.openal.ALContext;
import com.lwjglwrapper.openal.SoundBuffer;
import com.lwjglwrapper.openal.Source;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Welcome
 */
public class AudioHandler {
    private ALContext ctx;
    private Source source;
    private List<SoundBuffer> gameMusics;
    private boolean musicEnabled = true;

    public AudioHandler() {
        ctx = new ALContext();
        gameMusics = new ArrayList<>();
        source = new Source();
        
        gameMusics.add(ctx.loadSound(AudioHandler.class, "/musics/ambient1.ogg"));
        
        source.setSound(gameMusics.get(0));
        source.play();
    }

    public void dispose() {
        source.dispose();
        gameMusics.forEach(SoundBuffer::dispose);
        ctx.dispose();
    }

    public boolean invert() {
        musicEnabled = !musicEnabled;
        if(musicEnabled) {
            source.play();
        } else {
            source.stop();
        }
        return musicEnabled;
    }
    
    
}
