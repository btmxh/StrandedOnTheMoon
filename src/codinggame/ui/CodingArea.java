/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.ui;

import codinggame.CodingGame;
import codinggame.states.InputProcessor;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.nanovg.NVGFont;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.geom.shapes.RoundRect;
import com.lwjglwrapper.utils.math.MathUtils;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.Stage;
import com.lwjglwrapper.utils.ui.TextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.joml.Rectanglef;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class CodingArea extends TextField {

    static final float FONT_SIZE = 16, LINE_HEIGHT = FONT_SIZE * 1.25f;
    static final float OFFSET = 10;
    static final float TEXT_OFFSET = 20;
    static final float SCROLL_BAR_OFFSET = 4;
    
    public static NVGFont textFont;

    public static void initFont() {
        textFont = LWJGL.graphics.createFont("res/ProggyClean.ttf", "Proggy Clean");
    }

    private Caret caret;
    private String[] lines = new String[]{""};
    private Rectanglef bounds, scissor;
    private boolean editable = true;
    private boolean lineNumbered = false;
    
    //Debug
    private KeyTimer deleteTimer = new KeyTimer(GLFW.GLFW_KEY_DELETE, 500, 50);
    private KeyTimer leftTimer = new KeyTimer(GLFW.GLFW_KEY_LEFT, 500, 50);
    private KeyTimer rightTimer = new KeyTimer(GLFW.GLFW_KEY_RIGHT, 500, 50);
    private KeyTimer tabTimer = new KeyTimer(GLFW.GLFW_KEY_TAB, 500, 50);
    private KeyTimer upTimer = new KeyTimer(GLFW.GLFW_KEY_UP, 500, 50);
    private KeyTimer downTimer = new KeyTimer(GLFW.GLFW_KEY_DOWN, 500, 50);
    
    private VerticalScrollBar verticalScrollBar;
    private HorizontalScrollBar horizontalScrollBar;
    
    private static final int DELETE_BY_DEL_BUTTON = 2;

    public CodingArea(Stage stage, Rectanglef bounds) {
        super(stage, false);
        this.bounds = bounds;
        shapes.reset().setAll(new Rect(bounds))
                .setAllFills(IColor.WHITE)
                .setStroke(IColor.AQUA, SELECTED)
                .setStroke(IColor.ALICEBLUE, CLICKED)
                .setStroke(IColor.GRAY, UNSELECTED)
                .setStroke(IColor.DARKGRAY, UNSELECTED_HOVERING)
                .setAllAfterPaints((g) -> {
                    g.push();
                    g.translate(bounds.minX, bounds.minY);
                    verticalScrollBar.render(g);
                    horizontalScrollBar.render(g);
                    
                    g.setScissorArea(0, 0, scissor.maxX - scissor.minX, scissor.maxY - scissor.minY);
                    g.translate(OFFSET * 1.5f, OFFSET * 2.5f);
                    textFont.use();
                    g.textAlign(NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_BASELINE);
                    g.textPaint(IColor.RED);
                    g.textSize(FONT_SIZE);
                    g.translate((lineNumbered? TEXT_OFFSET:0) - horizontalScrollBar.calculateShiftAmount(),
                            -verticalScrollBar.calculateShiftAmount());
                    for (int i = 0; i < lines.length; i++) {
                        if(lineNumbered) {
                            g.textAlign(NanoVG.NVG_ALIGN_RIGHT);
                            g.translate(-TEXT_OFFSET, 0);
                            g.text((i+1) + "", 15, 0);
                            g.textAlign(NanoVG.NVG_ALIGN_LEFT);
                            g.translate(TEXT_OFFSET, 0);
                        }
                        g.text(lines[i].replace("\t", "   "), 0, 0);
                        if (i == caret.lineIndex)   caret.render(g);
                        g.translate(0, LINE_HEIGHT);
                    }
                    
                    g.pop();
                }).construct(false);
        caret = new Caret();
        super.setTextOnChangeListener((string, mode) -> {
            textFont.use();
            LWJGL.graphics.textSize(FONT_SIZE);
            Optional<Float> maxWidth = Stream.of(lines).map(LWJGL.graphics::textLength).reduce(Float::max);
            horizontalScrollBar.longestLineWidth = maxWidth.isPresent()? maxWidth.get():0;
            if(mode != DELETE_BY_DEL_BUTTON) {
                int sign = mode * 2 - 1;
                caret.characterIndex += sign * string.length();
            }
            caret.updateCaret();
        });
        verticalScrollBar = new VerticalScrollBar(bounds.maxX - bounds.minX - OFFSET - SCROLL_BAR_OFFSET, SCROLL_BAR_OFFSET, OFFSET, bounds.maxY - bounds.minY - 2 * SCROLL_BAR_OFFSET);
        horizontalScrollBar = new HorizontalScrollBar(SCROLL_BAR_OFFSET, bounds.maxY - bounds.minY - OFFSET - SCROLL_BAR_OFFSET,
                bounds.maxX - bounds.minX - 2 * SCROLL_BAR_OFFSET, OFFSET);
        stage.window.getMouse().getScrollCallback().add(new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                verticalScrollBar.scroll(yoffset);
            }
        });
        scissor = new Rectanglef(0, 0, bounds.maxX - bounds.minX - OFFSET - SCROLL_BAR_OFFSET * 2, bounds.maxY - bounds.minY - OFFSET - SCROLL_BAR_OFFSET * 2);
        
    }

    @Override
    public void tick() {
        super.tick();
        verticalScrollBar.tick();
        horizontalScrollBar.tick();
        if(!selected)   return;
        caret.updateTimer((float) LWJGL.currentLoop.getDeltaTime());
        if(deleteTimer.test(LWJGL.keyboard)) {
            deleteTextByDelButton();
        } else if(leftTimer.test(LWJGL.keyboard)) {
            caret.increasePosition(-1);
        } else if(rightTimer.test(LWJGL.keyboard)) {
            caret.increasePosition(1);
        } else if(upTimer.test(LWJGL.keyboard)) {
            caret.increaseLine(-1);
        } else if(downTimer.test(LWJGL.keyboard)) {
            caret.increaseLine(1);
        }
        if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_HOME)) {
            caret.characterIndex = -1;
            caret.updateCaret();
        } else if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_END)) {
            caret.characterIndex = text.length() - 1;
            caret.updateCaret();
        }
        if(tabTimer.test(LWJGL.keyboard)) {
            appendText("\t");
        }
        if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            caret.goTo(LWJGL.mouse.getCursorX() - bounds.minX - OFFSET * 1.5f - (lineNumbered? TEXT_OFFSET:0), LWJGL.mouse.getCursorY() - bounds.minY - OFFSET * 2.5f + verticalScrollBar.calculateShiftAmount());
        }
        }

    @Override
    public void appendText(String append) {
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

    @Override
    public void render(NVGGraphics g) {
        scissor = new Rectanglef(0, 0, bounds.maxX - bounds.minX - OFFSET - SCROLL_BAR_OFFSET * 2, bounds.maxY - bounds.minY - OFFSET - SCROLL_BAR_OFFSET * 2);
        super.render(g);
    }

    public void setLineNumbered(boolean lineNumbered) {
        this.lineNumbered = lineNumbered;
    }

    public class Caret {

        private static final float VISIBLE_TIME = 0.3f;
        private static final float offsetFactor = 0.25f;
        
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
            x = LWJGL.graphics.textLength(lines[lineIndex].substring(0, lineCharacterIndex + 1).replace("\t", "   "));
            
            float y = lineIndex * LINE_HEIGHT;
            
            float ux = x - horizontalScrollBar.calculateShiftAmount();
            y -= verticalScrollBar.calculateShiftAmount();
            
            float y1 = y + FONT_SIZE * offsetFactor;
            float y2 = y - FONT_SIZE * (1 - offsetFactor);
            
            Vector2f p1 = new Vector2f(ux, y1);
            Vector2f p2 = new Vector2f(ux, y2);
            //Rect scissor = new Rect(CodingArea.this.scissor);
            if(ux < scissor.minX) {
                float shift = ux - scissor.minX + 10;
                horizontalScrollBar.shift(-shift);
            } else if(x > scissor.maxX) {
                float shift = scissor.maxX - ux - 1000;
                horizontalScrollBar.shift(-shift);
            }
            if(y1 < scissor.minY) {
                float shift = scissor.minY - y2 + 10;
                verticalScrollBar.shift(-shift);
            }
        }
        
//        private void goTo

        private void increaseLine(int lineCount) {
            int newLineIndex = lineIndex + lineCount;
            if(newLineIndex >= lines.length || newLineIndex < 0)    return;
            int newLineCharacterIndex = MathUtils.clamp(-1, lineCharacterIndex, lines[newLineIndex].length() - 1);
            characterIndex = calculateCharacterIndex(newLineIndex, newLineCharacterIndex);
            updateCaret();
        }
        
        private void goTo(float x, float y) {
            System.out.println(x);
            x += horizontalScrollBar.calculateShiftAmount();
            y += verticalScrollBar.calculateShiftAmount();
            System.out.println(x + " " + y);
            int newLineIndex = (int) Math.floor(y / LINE_HEIGHT) + 1;
            if(newLineIndex >= lines.length || newLineIndex < 0) {
                characterIndex = text.length() - 1;
                updateCaret();
                return;
            }
            int newLineCharacterIndex = MathUtils.clamp(-1, (int) Math.floor(x / (FONT_SIZE * 0.6f)), lines[newLineIndex].length() - 1);
            characterIndex = calculateCharacterIndex(newLineIndex, newLineCharacterIndex);
            updateCaret();
            System.out.println(this.x);
        }
        
        private int calculateCharacterIndex(int lineIndex, int lineCharacterIndex) {
            int characterIndex = -1;
            for (int i = 0; i < lineIndex; i++) {
                String line = lines[i];
                characterIndex += line.length() + 1;
            }
            characterIndex += lineCharacterIndex + 1;
            return characterIndex;
        }
    
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

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public class VerticalScrollBar {
        private float orgX, orgY;
        private float width, height;
        private float shiftRatio;
        
        private Rect currentBounds;
        private boolean inBounds = false;
        private float savedCursorY, savedShiftAmount;
        
        private boolean scrollDisabled;

        public VerticalScrollBar(float orgX, float orgY, float width,
                float height) {
            this.orgX = orgX;
            this.orgY = orgY;
            this.width = width;
            this.height = height;
            this.shiftRatio = 0;
            scrollDisabled = false;
        }

        public void tick() {
            float totalHeight = lines.length * LINE_HEIGHT;
            scrollDisabled = bounds.maxY - bounds.minY - OFFSET * 1.5f >= totalHeight;
            if(scrollDisabled) {
                shiftRatio = 0;
                return;
            }
            float barHeight = (bounds.maxY - bounds.minY) / totalHeight * height;
            float barWidth = width;
            float barX = orgX;
            float mgtY = height - barHeight;
            float barY = orgY + mgtY * shiftRatio;
            currentBounds = new RoundRect(barX, barY, barWidth, barHeight, 2f);
            Vector2f vector = detransformedMousePosition.sub(bounds.minX, bounds.minY, new Vector2f());
            boolean contains = currentBounds.contains(vector);
            if(contains) {
                stage.setStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            }
            if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                inBounds = contains;
                if(inBounds) {
                    savedCursorY = LWJGL.mouse.getCursorY();
                    savedShiftAmount = shiftRatio;
                }
            }
            if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                inBounds = false;
            }
            if(LWJGL.mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && inBounds) {
                shiftRatio = savedShiftAmount + (LWJGL.mouse.getCursorY() - savedCursorY) * .005f;
                shiftRatio = MathUtils.clamp(0f, shiftRatio, 1f);
            }
            if(LWJGL.keyboard.keyReleased(GLFW.GLFW_KEY_F5)) {
                shiftRatio = 0.2f;
            }
        }
        
        public void render(NVGGraphics g) {
            if(scrollDisabled)   return;
            currentBounds.render(g);
            g.fill(new IColor(0.2f));
            g.stroke(IColor.BLACK);
        }

        private float calculateShiftAmount() {
            float totalHeight = lines.length * LINE_HEIGHT;
            return shiftRatio * (totalHeight - bounds.maxY + bounds.minY + OFFSET * 2.5f);
        }

        private void scroll(double yoffset) {
            if(scrollDisabled)   return;
            if(CodingGame.getInstance().getGameState().getInputProcessor() != InputProcessor.GAME_UI)   return;
            if(!new Rect(bounds).contains(LWJGL.mouse.getCursorPosition())) return;
            shiftRatio -= yoffset * 0.05f;
            shiftRatio = MathUtils.clamp(0f, shiftRatio, 1f);
        }

        private void shift(float f) {
        }

    }
    
    public class HorizontalScrollBar {
        private float orgX, orgY;
        private float width, height;
        private float shiftRatio;
        
        private Rect currentBounds;
        private boolean inBounds = false;
        private float savedCursorX, savedShiftAmount;
        
        private boolean scrollDisabled;
        private float longestLineWidth;

        public HorizontalScrollBar(float orgX, float orgY, float width,
                float height) {
            this.orgX = orgX;
            this.orgY = orgY;
            this.width = width;
            this.height = height;
            this.shiftRatio = 0;
            scrollDisabled = false;
        }

        public void tick() {
//            float totalWidth = lines.length * LINE_HEIGHT;
            scrollDisabled = bounds.maxX - bounds.minX - OFFSET * 2.5f >= longestLineWidth;
            if(scrollDisabled) {
                shiftRatio = 0;
                return;
            }
            float barHeight = height;
            float barWidth = (bounds.maxX - bounds.minX) / longestLineWidth * width;
            float mgtX = width - barWidth;
            float barX = orgX + mgtX * shiftRatio;
            float barY = orgY;
            currentBounds = new RoundRect(barX, barY, barWidth, barHeight, 2f);
            Vector2f vector = detransformedMousePosition.sub(bounds.minX, bounds.minY, new Vector2f());
            boolean contains = currentBounds.contains(vector);
            if(contains) {
                stage.setStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            }
            if(LWJGL.mouse.mousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                inBounds = contains;
                if(inBounds) {
                    savedCursorX = LWJGL.mouse.getCursorX();
                    savedShiftAmount = shiftRatio;
                }
            }
            if(LWJGL.mouse.mouseReleased(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                inBounds = false;
            }
            if(LWJGL.mouse.mouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && inBounds) {
                shiftRatio = savedShiftAmount + (LWJGL.mouse.getCursorX() - savedCursorX) * .005f;
                shiftRatio = MathUtils.clamp(0f, shiftRatio, 1f);
            }
            
        }
        
        public void render(NVGGraphics g) {
            if(scrollDisabled)   return;
            currentBounds.render(g);
            g.fill(new IColor(0.2f));
            g.stroke(IColor.BLACK);
        }

        private float calculateShiftAmount() {
            float shift = shiftRatio * (longestLineWidth - bounds.maxX + bounds.minX + OFFSET * 2.5f);
            return shift;
        }

        private void shift(float amt) {
            shiftRatio += amt / (longestLineWidth - bounds.maxX + bounds.minX + OFFSET * 2.5f);
            shiftRatio = MathUtils.clamp(0f, shiftRatio, 1f);
        }
    }
}
