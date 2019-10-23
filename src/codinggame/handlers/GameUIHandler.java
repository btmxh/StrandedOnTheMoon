/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.handlers;

import codinggame.CodingGame;
import codinggame.lang.JavaParser;
import codinggame.lang.Parser;
import codinggame.map.GameMap.ChooseTile;
import codinggame.map.MapCell;
import codinggame.map.cells.InventoryCell;
import codinggame.objs.Battery;
import codinggame.objs.Inventory;
import codinggame.objs.items.Item;
import codinggame.objs.items.ItemType;
import codinggame.objs.items.equipments.EquipmentSlot;
import codinggame.objs.modules.Module;
import codinggame.objs.robots.Robot;
import codinggame.states.GameState;
import codinggame.states.InputProcessor;
import codinggame.ui.CodingArea;
import codinggame.ui.IButton;
import codinggame.ui.Tab;
import codinggame.ui.TabbedPanel;
import com.lwjglwrapper.LWJGL;
import com.lwjglwrapper.display.Window;
import com.lwjglwrapper.nanovg.NVGGraphics;
import com.lwjglwrapper.nanovg.NVGImage;
import com.lwjglwrapper.nanovg.paint.Paint;
import com.lwjglwrapper.utils.IColor;
import com.lwjglwrapper.utils.Utils;
import com.lwjglwrapper.utils.floats.GLFloats;
import com.lwjglwrapper.utils.floats.GLFloat;
import com.lwjglwrapper.utils.geom.PaintedShape;
import com.lwjglwrapper.utils.geom.Shape;
import com.lwjglwrapper.utils.geom.shapes.GLRect;
import com.lwjglwrapper.utils.geom.shapes.Rect;
import com.lwjglwrapper.utils.geom.shapes.RoundRect;
import com.lwjglwrapper.utils.ui.Button;
import com.lwjglwrapper.utils.ui.Component;
import com.lwjglwrapper.utils.ui.Panel;
import com.lwjglwrapper.utils.ui.Stage;
import com.lwjglwrapper.utils.ui.TextField;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.nanovg.NanoVG;

/**
 *
 * @author Welcome
 */
public class GameUIHandler {
    private Stage stage;
    
    private NVGImage closeIcon;
    
    private final CodingArea codingArea, loggerArea;
    private final IButton playButton;
    private final IButton openCodeButton;
    private final Button closeButton;
    private final TabbedPanel infoBar;
    private final Tab tileInventory;
    
    private Parser parser;
    private JavaParser javaParser;
    private GameState game;
    
    private PrintWriter loggerWriter;

    public GameUIHandler(Window window, GameState game) {
        initStaticVariables();
        parser = new Parser(game);
        javaParser = new JavaParser(game);
        stage = window.getStage();
        this.game = game;
        
        try {
            closeIcon = LWJGL.graphics.createNanoVGImageFromResource(Utils.ioResourceToByteBuffer(GameUIHandler.class.getResourceAsStream("/close1.png"), 8 * 1024), NanoVG.NVG_IMAGE_GENERATE_MIPMAPS);
        } catch (IOException ex) {
            Logger.getLogger(GameUIHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        infoBar = new TabbedPanel(stage, true);
        
        Panel robotInfoBar = new Panel(stage, false);
        robotInfoBar.getShapeStates().reset()
                .setAll(new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(IColor.BLACK)
                .setAllFills(new IColor(0.1f))
                .construct(false);
        
        Component robotPanelInfo = new Component(stage, false, 1);
        robotPanelInfo.getShapeStates().setAll(Shape.EMPTY).setAllAfterPaints((g) -> {
            g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get());
            Robot robot = (Robot) game.getSelectedObject();
            g.setUpText(CodingArea.textFont, IColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
            g.translate(0, 60);
            g.text(robot.getTypeName(), 10, 0);
            g.translate(0, 30);
            g.text("Name: " + robot.getName(), 10, 0);
            g.translate(0, 30);
            Battery battery = robot.getBattery();
            g.text("Battery: " + (int) battery.getEnergy() + "/" + (int) battery.getMaxEnergyCapacity(), 10, 0);
            g.translate(0, 30);
            g.text("Position: x=" + robot.getTileX() + ", y=" + robot.getTileY(), 10, 0);
            g.translate(0, 30);
            g.text("Capacity: " + String.format(Locale.US, "%.1f", robot.getInventory().getCurrentCapacity())
                    + "/" + robot.getInventory().getMaxCapacity() + " (kg)", 10, 0);
            g.translate(0, 30);
        }).construct(false);
        
        Tab robotInv = new Tab(stage, "Inventory", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                Robot robot = (Robot) game.getSelectedObject();
                for (Map.Entry<EquipmentSlot, Item> entry : robot.getInventory().getEquipments().entrySet()) {
                    EquipmentSlot slot = entry.getKey();
                    Item item = entry.getValue();

                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(slot.getColor());
                    g.setUpText(CodingArea.textFont, IColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
                for (Map.Entry<ItemType, Item> entry : robot.getInventory().getItems().entrySet()) {
                    Item item = entry.getValue();

                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new IColor(0.2f));
                    g.setUpText(CodingArea.textFont, IColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.image(item.getItemType().getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
            }
        };
        Tab modules = new Tab(stage, "Modules", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                Robot robot = (Robot) game.getSelectedObject();
                for (Module module : robot.getInventory().getModules()) {
                    if(module == null)  continue;
                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new IColor(0.2f));
                    g.setUpText(CodingArea.textFont, IColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(module.getName(), 10, 15);
                    g.image(module.getNanoVGTexture(), INFO_BAR_WIDTH.get() - 30, 0, 30, 30);
                    g.translate(0, 32);
                }
            }
        };
        
        Panel tileInfoBar = new Panel(stage, false);
        tileInfoBar.getShapeStates().reset()
                .setAll(new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, INFO_BAR_HEIGHT))
                .setAllStrokes(IColor.BLACK)
                .setAllFills(new IColor(0.1f))
                .setAllBeforePaints((g) -> g.translate(INFO_BAR_X.get(), INFO_BAR_Y.get()))
                .setAllAfterPaints((g) -> {
                    ChooseTile tile = (ChooseTile) game.getSelectedObject();
                    MapCell cell = tile.layer.getTileAt(tile.x, tile.y);
                    g.translate(0, 60);
                    g.setUpText(CodingArea.textFont, IColor.WHITE, 20, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
                    g.text("Tile: " + cell.getTileType().getID(), 10, 0);
                    g.translate(0, 30);
                    g.text("Position: x=" + tile.x + " y=" + tile.y, 10, 0);
                    g.translate(0, 30);
                }).construct(false);
        
        tileInventory = new Tab(stage, "Inventory", new Rect(GLFloats.ZERO, GLFloats.ZERO, INFO_BAR_WIDTH, GLFloat.memValue(30)), false) {
            @Override
            public void renderContent(NVGGraphics g) {
                ChooseTile tile = (ChooseTile) game.getSelectedObject();
                InventoryCell cell = (InventoryCell) tile.layer.getTileAt(tile.x, tile.y);
                Inventory inv = cell.getInventory();
                for (Map.Entry<ItemType, Item> entry : inv.getItems().entrySet()) {
                    Item item = entry.getValue();
                    
                    g.rect(0, 0, INFO_BAR_WIDTH.get(), 30);
                    g.fill(new IColor(0.2f));
                    g.setUpText(CodingArea.textFont, IColor.WHITE, 16, NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT);
                    g.text(item.toString(), 10, 15);
                    g.translate(0, 32);
                }
            }
        };
        
        codingArea = new CodingArea(stage, new GLRect(
                GLFloat.sub(GLFloats.w_fracOfWidth(GLFloat.memValue(0.73f)), INFO_BAR_WIDTH),
                GLFloat.sub(GLFloats.w_fracOfHeight(GLFloat.memValue(0.35f)), GLFloat.memValue(10)),
                GLFloats.w_fracOfWidth(GLFloat.memValue(0.27f)),
                GLFloats.w_fracOfHeight(GLFloat.memValue(0.5f))
        ));
        codingArea.setVisible(false);
        codingArea.appendText("public void main(){\n\t" + (CodingGame.arg == null? "Display.setSize(1440, 878);":CodingGame.arg)+"\n}");
        codingArea.setLineNumbered(true);
        loggerArea = new CodingArea(stage, new GLRect(
                GLFloat.sub(GLFloats.w_fracOfWidth(GLFloat.memValue(0.73f)), INFO_BAR_WIDTH),
                GLFloats.w_fracOfHeight(GLFloat.memValue(0.85f)),
                GLFloats.w_fracOfWidth(GLFloat.memValue(0.27f)),
                GLFloats.w_fracOfHeight(GLFloat.memValue(0.15f))
        ));
        loggerArea.setEditable(false);
        loggerArea.setVisible(false);
        println("Output:");
        openCodeButton = new IButton(stage, false, new GLRect(
                GLFloat.memValue(10), GLFloats.w_fromFarXAxis(GLFloat.memValue(310)), GLFloat.memValue(200), GLFloat.memValue(40)
        ), new Paint[]{new IColor(0.5f), new IColor(0.6f), new IColor(0.2f)}, IColor.WHITE, IColor.WHITE);
        //openCodeButton.setVisible(false);
        openCodeButton.setText("Open Code");
        openCodeButton.setOnClickListener(new Button.OnClickListener() {
            private boolean opened = false;
            @Override
            public void action(Stage stage, Button comp, int mode) {
                opened = !opened;
                openCodeButton.setText(opened? "Close Code":"Open Code");
                codingArea.setVisible(opened);
                loggerArea.setVisible(opened);
            }
        });
        playButton = new IButton(stage, false, new GLRect(
                GLFloat.memValue(10), GLFloats.w_fromFarXAxis(GLFloat.memValue(260)), GLFloat.memValue(200), GLFloat.memValue(40)
        ), new Paint[]{new IColor(0.5f), new IColor(0.6f), new IColor(0.2f)}, IColor.WHITE, IColor.WHITE);
        playButton.setText("Execute");
        playButton.setOnClickListener((Stage s, Button comp, int mode) -> {
            if(game.getRobotHandler().getCurrentRobot().isRunning()) {
                game.getCommandHandler().stop(game.getRobotHandler().getCurrentRobot());
                playButton.setText("Execute");
            } else {
                String code = codingArea.getText();
                Runnable main = javaParser.parse(code, game.getRobotHandler().getCurrentRobot());
                Thread thread = new Thread(main);
                game.getCommandHandler().addThread(game.getRobotHandler().getCurrentRobot(), thread);
                playButton.setText("Stop");
            }
        });
        
        robotInfoBar.addChild(codingArea);
        robotInfoBar.addChild(loggerArea);
        robotInfoBar.addChild(robotPanelInfo);
        robotInfoBar.addChild(openCodeButton);
        robotInfoBar.addChild(playButton);
        robotInfoBar.addChild(robotInv);
        robotInfoBar.addChild(modules);
        
        tileInfoBar.addChild(tileInventory);
        
        infoBar.getPanels().addAll(Arrays.asList(robotInfoBar, tileInfoBar));
        
        closeButton = new Button(stage, true);
        GLRect bounds = new GLRect(GLFloat.add(INFO_BAR_X, GLFloat.memValue(10)), GLFloat.add(INFO_BAR_Y, GLFloat.memValue(10)), GLFloat.memValue(32), GLFloat.memValue(32));
        GLRect imageBounds = new GLRect(GLFloat.add(INFO_BAR_X, GLFloat.memValue(5)), GLFloat.add(INFO_BAR_Y, GLFloat.memValue(5)), GLFloat.memValue(42), GLFloat.memValue(42));
        RoundRect rect = new RoundRect(imageBounds, GLFloat.memValue(4));
        closeButton.getShapeStates().setAll(new Rect(bounds, imageBounds))
                .setBeforePaint(g -> new PaintedShape(rect, new IColor(0.1f), null).render(g), Button.NORMAL)
                .setBeforePaint(g -> new PaintedShape(rect, new IColor(0.3f), null).render(g), Button.HOVER)
                .setBeforePaint(g -> new PaintedShape(rect, IColor.BLACK, null).render(g), Button.CLICKED)
                .setAllFills((id) -> LWJGL.graphics.imagePaint(closeIcon, bounds.getJOMLRect(), 0, 1).fill(id))
                .construct(false);
        closeButton.setOnClickListener((Stage stage1, Button comp, int mode) -> {
            closeInfoBar();
        });
                
    }
    
    public void tick(InputProcessor processor) {
        stage.tick();
        playButton.setText(game.getRobotHandler().getCurrentRobot().isRunning()? "Stop":"Execute");
    }
    
    public void render(NVGGraphics g) {
        Object selected = game.getSelectedObject();
        if(selected == null)    infoBar.select(-1);
        else if(selected instanceof Robot)  infoBar.select(0);
        else if(selected instanceof ChooseTile) {
            infoBar.select(1);
            ChooseTile tile = (ChooseTile) selected;
            tileInventory.setVisible(tile.layer.getTileAt(tile.x, tile.y) instanceof InventoryCell);
        }
        closeButton.setVisible(infoBar.getSelectedIndex() >= 0);
        g.begin();
//        g.image(ItemTypes.COPPER_ORE.getNanoVGTexture(), 40, 40, 128, 128, IColor.RED);
        g.setUpText(CodingArea.textFont, IColor.WHITE, 24, NanoVG.NVG_ALIGN_TOP | NanoVG.NVG_ALIGN_LEFT);
        g.text(game.getClock().getDisplayTime(), 10, 10);
        g.text(game.getInputProcessor().toString(), 10, 40);
        stage.render();
        g.end();
    }
    
    private static GLFloat INFO_BAR_WIDTH, INFO_BAR_HEIGHT, INFO_BAR_X, INFO_BAR_Y;
    
    private void initStaticVariables() {
        INFO_BAR_WIDTH = GLFloat.memValue(300);
        INFO_BAR_HEIGHT = () -> LWJGL.window.getHeight();
        INFO_BAR_X = GLFloats.w_fromFarYAxis(INFO_BAR_WIDTH);
        INFO_BAR_Y = GLFloats.ZERO;
        GLFloats.setWindow(LWJGL.window);
        GLFloats.setViewport(LWJGL.window.getViewport());
        
        LWJGL.window.getWindowCallbacks().getSizeCallback().add(1, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if((width == 0 && height == 0)) {
                    //User has just restore/minimize the window, not resizing it
                } else {
                    game.resize();
                }
            }
        });
    }
    
    public boolean clickedOn() {
        if(infoBar.getSelectedIndex() == -1)    return false;
        if(infoBar.getSelectedIndex() == 0) {
            if(codingArea.isVisible() && codingArea.clickedOn())    return true;
            else if(loggerArea.isVisible() && loggerArea.clickedOn())   return true;
        }
        Rect infoBarBounds = new Rect(INFO_BAR_X, INFO_BAR_Y, INFO_BAR_WIDTH, INFO_BAR_HEIGHT);
        return infoBarBounds.contains(LWJGL.mouse.getCursorPosition());
    }

    public void println(String string) {
        print(string + "\n");
    }
    
    public void print(String string) {
        loggerArea.getTextBuilder().append(string);
        loggerArea.getTextOnChangeListener().textOnChange(string, TextField.TextOnChangeListener.ADD);
    }
    
    public PrintWriter loggerWriter() {
        if(loggerWriter == null) {
            loggerWriter = new PrintWriter(new LoggerWriter());
        }
        return loggerWriter;
    }

    public void closeInfoBar() {
        game.select(null);
        game.setProcessor(InputProcessor.GAME);
    }
    
    private class LoggerWriter extends Writer{

        private LoggerWriter() {
            lock = new Object();
        }

        @Override
        public void write(int c) throws IOException {
            print((char) c + "");
        }
        
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            String append = new String(cbuf, off, len);
            print(append);
        }

        @Override
        public void write(String append) throws IOException {
            print(append);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            print(str.substring(off, off + len));
        }

        @Override
        public LoggerWriter append(CharSequence csq) throws IOException {
            write(csq == null? "null":csq.toString());
            return this;
        }

        @Override
        public LoggerWriter append(char c) throws IOException {
            write(c);
            return this;
        }

        @Override
        public LoggerWriter append(CharSequence csq, int start, int end) throws IOException {
            CharSequence cs = (csq == null ? "null" : csq);
            write(cs.subSequence(start, end).toString());
            return this;
        }

        @Override
        public String toString() {
            return loggerArea.getText();
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            super.write(cbuf);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
        
    }
}
