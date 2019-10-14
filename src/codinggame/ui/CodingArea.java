/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGFont;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.math.MathUtils;
import com.lwjglwrapper.utils.ui.Stage;
import com.lwjglwrapper.utils.ui.TextField;
import java.util.ArrayList;
import javafx.util.Pair;
import org.joml.Rectanglef;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class CodingArea extends TextField {

    static final float FONT_SIZE = 14, LINE_HEIGHT = FONT_SIZE * 1.25f;
    
    public static NVGFont textFont;

    public static void initFont() {
        textFont = LWJGL.graphics.createFont("res/ProggyClean.ttf", "Proggy Clean");
    }

    private Caret caret;
    private String[] lines = new String[]{""};
    private Rectanglef bounds;
    private boolean editable = true;
    
    //Debug
    private KeyTimer deleteTimer = new KeyTimer(GLFW.GLFW_KEY_DELETE, 500, 50);
    private KeyTimer leftTimer = new KeyTimer(GLFW.GLFW_KEY_LEFT, 500, 50);
    private KeyTimer rightTimer = new KeyTimer(GLFW.GLFW_KEY_RIGHT, 500, 50);
    
    private static final int DELETE_BY_DEL_BUTTON = 2;

    public CodingArea(Stage stage, Rectanglef bounds) {
        super(stage, true);
        float offset = 10;
        this.bounds = bounds;
        shapes.reset().setAll(new Rect(bounds))
                .setAllFills(IColor.WHITE)
                .setStroke(IColor.AQUA, SELECTED)
                .setStroke(IColor.ALICEBLUE, CLICKED)
                .setStroke(IColor.GRAY, UNSELECTED)
                .setStroke(IColor.DARKGRAY, UNSELECTED_HOVERING)
                .setAllAfterPaints((g) -> {
                    textFont.use();

                    g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_BASELINE);
                    g.textPaint(IColor.RED);
                    g.textSize(FONT_SIZE);
                    
                    g.push();
                    g.translate(bounds.minX + offset * 1.5f, bounds.minY + offset * 2.5f);
                    
                    for (int i = 0; i < lines.length; i++) {
                        g.text(lines[i], 0, 0);
                        if (i == caret.lineIndex)   caret.render(g);
                        g.translate(0, LINE_HEIGHT);
                    }
                    
                    g.pop();
                }).construct(false);
        caret = new Caret();
        super.setTextOnChangeListener((string, mode) -> {
            if(mode != DELETE_BY_DEL_BUTTON) {
                int sign = mode * 2 - 1;
                caret.characterIndex += sign * string.length();
            }
            caret.updateCaret();
        });
    }

    @Override
    public void tick() {
        super.tick();
        caret.updateTimer((float) LWJGL.currentLoop.getDeltaTime());
        if(deleteTimer.test(LWJGL.keyboard)) {
            deleteTextByDelButton();
        }
        if(leftTimer.test(LWJGL.keyboard)) {
            caret.increasePosition(-1);
        } else if(rightTimer.test(LWJGL.keyboard)) {
            caret.increasePosition(1);
        }
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_HOME)) {
            caret.characterIndex = -1;
            caret.updateCaret();
        } else if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_END)) {
            caret.characterIndex = text.length() - 1;
            caret.updateCaret();
        }
    }

    @Override
    protected void appendText(String append) {
        if(!editable)   return;
        getTextBuilder().insert(caret.characterIndex + 1, append);
        textOnChangeListener.textOnChange(append, TextOnChangeListener.ADD);
    }

    @Override
    protected void removeText(String delete) {
        if(caret.characterIndex + 1 - delete.length() < 0 || !editable)  return;
        getTextBuilder().delete(caret.characterIndex + 1 - delete.length(), caret.characterIndex + 1);
        textOnChangeListener.textOnChange(delete, TextOnChangeListener.DELETE);
    }
    
    protected void deleteTextByDelButton() {
        if(caret.characterIndex + 1 >= text.length())  return;
        char delete = text.charAt(caret.characterIndex + 1);
        text.deleteCharAt(caret.characterIndex + 1);
        textOnChangeListener.textOnChange(delete + "", DELETE_BY_DEL_BUTTON);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
            LWJGL.window.setStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        }
    }

    public boolean clickedOn() {
        return new Rect(bounds).contains(LWJGL.mouse.getCursorPosition());
    }

    public TextOnChangeListener getTextOnChangeListener() {
        return textOnChangeListener;
    }
    
    

    public class Caret {

        private static final float VISIBLE_TIME = 0.5f;
        
        private int characterIndex = -1; //caret is AFTER the character
        private float x = 0;
        
        private int lineIndex;
        private int lineCharacterIndex = -1;

        private boolean show = true;
        private float showTimer = 0;

        public Caret() {
        }

        public void updateTimer(float delta) {
            showTimer += delta;
            showTimer %= VISIBLE_TIME * 2;
            show = showTimer < VISIBLE_TIME;
        }
        
        public void render(NVGGraphics g) {
            if(!show | !selected)   return;
            final float offsetFactor = 0.25f;
            
            g.line(x, FONT_SIZE * offsetFactor, x, -FONT_SIZE * (1 - offsetFactor));
            g.stroke(IColor.RED);
        }

        private void increasePosition(int incr) {
            characterIndex += incr;
            characterIndex = MathUtils.clamp(-1, characterIndex, text.length() - 1);
            updateCaret();
        }
        
        private void updateCaret() {
            Pair<String[], Integer> split = split(text.toString(), '\n', caret.characterIndex);
            lines = split.getKey();
            caret.lineCharacterIndex = split.getValue();
            if (caret.characterIndex < 0) {
                caret.lineIndex = 0;
            } else {
                caret.lineIndex = (int) text.substring(0, caret.characterIndex).chars().filter((c) -> c == (int) '\n').count();
                if(caret.lineCharacterIndex == -1)   caret.lineIndex++;
            }
            textFont.use();
            LWJGL.graphics.textSize(FONT_SIZE);
            x = LWJGL.graphics.textLength(lines[lineIndex].substring(0, lineCharacterIndex + 1));
        }
        
//        private void goTo
    
    }

    private static Pair<String[], Integer> split(String text, char split,
            int index) {
        ArrayList<String> splitList = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int countIndex = -1;
        int returnIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == split) {
                splitList.add(current.toString());
                countIndex = -1;
                current = new StringBuilder();
            } else {
                current.append(c);
                countIndex++;
            }
            if (i == index) {
                returnIndex = countIndex;
            }
        }
        splitList.add(current.toString());
        return new Pair<>(splitList.toArray(new String[0]), returnIndex);
    }
    
    public class VerticalScrollBar {
        private Rectanglef bounds;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
